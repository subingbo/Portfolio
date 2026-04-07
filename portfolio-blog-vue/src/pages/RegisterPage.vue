<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import { mockBrand, mockNav, mockSocial } from '../blog/mock/blogMock'
import { fetchCaptcha, registerUser, type CaptchaVo } from '../api/authApi'

const router = useRouter()

const username = ref('')
const password = ref('')
const password2 = ref('')
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

async function onSubmit() {
  errorMsg.value = null
  if (password.value !== password2.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  submitting.value = true
  try {
    const need =
      captcha.value?.captchaEnabled && captcha.value.uuid != null
    await registerUser({
      username: username.value.trim(),
      password: password.value,
      ...(need ?
        { code: code.value.trim(), uuid: captcha.value!.uuid! }
      : {}),
    })
    await router.push({ path: '/login', query: { registered: '1' } })
  } catch (e) {
    errorMsg.value = e instanceof Error ? e.message : '注册失败'
    await loadCaptcha()
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
      <h1 class="font-serif text-3xl font-semibold text-stone-900">注册</h1>
      <p class="mt-2 text-sm text-stone-600">
        使用 RuoYi
        <code class="rounded bg-stone-100 px-1 text-xs">POST /auth/register</code>
        ；需在后台开启注册功能。
      </p>

      <form class="mt-10 space-y-6" @submit.prevent="onSubmit">
        <div>
          <label :class="labelClass" for="reg-user">用户名</label>
          <input
            id="reg-user"
            v-model="username"
            type="text"
            autocomplete="username"
            required
            minlength="2"
            maxlength="30"
            :class="inputClass"
          />
        </div>
        <div>
          <label :class="labelClass" for="reg-pass">密码</label>
          <input
            id="reg-pass"
            v-model="password"
            type="password"
            autocomplete="new-password"
            required
            minlength="5"
            maxlength="30"
            :class="inputClass"
          />
        </div>
        <div>
          <label :class="labelClass" for="reg-pass2">确认密码</label>
          <input
            id="reg-pass2"
            v-model="password2"
            type="password"
            autocomplete="new-password"
            required
            :class="inputClass"
          />
        </div>
        <template v-if="captcha?.captchaEnabled">
          <div>
            <label :class="labelClass" for="reg-code">验证码</label>
            <div class="mt-1 flex gap-3">
              <input
                id="reg-code"
                v-model="code"
                type="text"
                required
                :class="inputClass + ' flex-1'"
                placeholder="若为算式请填计算结果"
              />
              <button
                type="button"
                class="shrink-0 rounded-lg border border-stone-200 bg-white p-1 shadow-sm"
                @click="loadCaptcha"
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
          {{ submitting ? '提交中…' : '注册' }}
        </button>
      </form>

      <p class="mt-8 text-sm text-stone-600">
        已有账号？
        <RouterLink
          to="/login"
          class="font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-2"
        >
          登录
        </RouterLink>
      </p>
    </main>
  </div>
</template>
