<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import { mockBrand, mockNav, mockSocial } from '../blog/mock/blogMock'
import { fetchCaptcha, loginByPassword } from '../api/authApi'
import type { CaptchaVo } from '../api/authApi'
import { setAccessToken } from '../auth/token'

const router = useRouter()

const username = ref('')
const password = ref('')
const code = ref('')
const captcha = ref<CaptchaVo | null>(null)
const captchaSrc = ref('')
const submitting = ref(false)
const errorMsg = ref<string | null>(null)

const inputClass =
  'mt-1 w-full rounded-lg border border-stone-200 bg-white px-3 py-2 text-stone-800 shadow-sm focus:border-emerald-600 focus:outline-none focus:ring-2 focus:ring-emerald-600/30'
const labelClass = 'block text-sm font-medium text-stone-700'

function applyCaptchaImg(vo: CaptchaVo) {
  if (!vo.img) {
    captchaSrc.value = ''
    return
  }
  captchaSrc.value = vo.img.startsWith('data:') ? vo.img : `data:image/png;base64,${vo.img}`
}

async function loadCaptcha() {
  try {
    captcha.value = await fetchCaptcha()
    code.value = ''
    if (captcha.value) applyCaptchaImg(captcha.value)
  } catch {
    captcha.value = { captchaEnabled: false }
    captchaSrc.value = ''
  }
}

onMounted(loadCaptcha)

async function refreshCaptcha() {
  await loadCaptcha()
}

async function onSubmit() {
  errorMsg.value = null
  submitting.value = true
  try {
    const need =
      captcha.value?.captchaEnabled && captcha.value.uuid != null
    const vo = await loginByPassword({
      username: username.value.trim(),
      password: password.value,
      ...(need ?
        { code: code.value.trim(), uuid: captcha.value!.uuid! }
      : {}),
    })
    const tok = vo.access_token
    if (!tok) {
      throw new Error('登录响应缺少 access_token')
    }
    setAccessToken(tok)
    await router.push('/')
  } catch (e) {
    errorMsg.value = e instanceof Error ? e.message : '登录失败'
    await refreshCaptcha()
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-[#faf8f3] text-stone-800 antialiased">
    <BlogHeader
      :brand="mockBrand"
      :nav-items="mockNav"
      :social-links="mockSocial"
    />
    <main class="mx-auto max-w-md px-5 py-16 md:px-8">
      <h1 class="font-serif text-3xl font-semibold text-stone-900">登录</h1>
      <p class="mt-2 text-sm text-stone-600">
        使用 RuoYi 接口
        <code class="rounded bg-stone-100 px-1 text-xs">POST /auth/login</code>
        （密码模式 + 可选验证码，与加密配置一致）
      </p>

      <form class="mt-10 space-y-6" @submit.prevent="onSubmit">
        <div>
          <label :class="labelClass" for="login-user">用户名</label>
          <input
            id="login-user"
            v-model="username"
            type="text"
            autocomplete="username"
            required
            :class="inputClass"
          />
        </div>
        <div>
          <label :class="labelClass" for="login-pass">密码</label>
          <input
            id="login-pass"
            v-model="password"
            type="password"
            autocomplete="current-password"
            required
            :class="inputClass"
          />
        </div>
        <template v-if="captcha?.captchaEnabled">
          <div>
            <label :class="labelClass" for="login-code">验证码</label>
            <div class="mt-1 flex gap-3">
              <input
                id="login-code"
                v-model="code"
                type="text"
                required
                :class="inputClass + ' flex-1'"
                placeholder="若为算式请填计算结果"
              />
              <button
                type="button"
                class="shrink-0 rounded-lg border border-stone-200 bg-white p-1 shadow-sm"
                @click="refreshCaptcha"
              >
                <img
                  v-if="captchaSrc"
                  :src="captchaSrc"
                  alt="验证码"
                  class="h-12 w-[8.5rem] object-cover"
                />
              </button>
            </div>
          </div>
        </template>

        <p v-if="errorMsg" class="text-sm text-red-800">{{ errorMsg }}</p>

        <button
          type="submit"
          class="w-full rounded-xl bg-emerald-800 px-4 py-3 text-sm font-medium text-white shadow hover:bg-emerald-900 disabled:opacity-60"
          :disabled="submitting"
        >
          {{ submitting ? '登录中…' : '登录' }}
        </button>
      </form>

      <p class="mt-8 text-sm text-stone-600">
        没有账号？
        <RouterLink
          to="/register"
          class="font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-2"
        >
          注册
        </RouterLink>
        <span class="mx-2 text-stone-400">·</span>
        <RouterLink
          to="/"
          class="font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-2"
        >
          返回首页
        </RouterLink>
      </p>
    </main>
  </div>
</template>
