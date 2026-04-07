<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import { mockBrand, mockNav, mockSocial } from '../blog/mock/blogMock'
import { fetchPortfolioProjectPage } from '../api/portfolioApi'
import type { PortfolioProjectVo } from '../api/types/backend'
import { toYmd } from '../api/adapters'

const projects = ref<PortfolioProjectVo[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const titleClass =
  'font-serif text-base font-semibold text-emerald-950 transition hover:text-emerald-800'

onMounted(async () => {
  loading.value = true
  error.value = null
  try {
    const { rows } = await fetchPortfolioProjectPage({ pageNum: 1, pageSize: 100 })
    projects.value = rows
  } catch (e) {
    projects.value = []
    error.value =
      e instanceof Error ? e.message : '无法加载项目列表，请确认后端与代理配置'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-screen bg-[#faf8f3] text-stone-800 antialiased">
    <BlogHeader
      :brand="mockBrand"
      :nav-items="mockNav"
      :social-links="mockSocial"
    />
    <main class="border-t border-emerald-900/5 px-5 py-14 md:px-8 md:py-20">
      <div class="mx-auto max-w-3xl lg:ml-4">
        <h1 class="font-serif text-3xl font-semibold text-stone-900 md:text-4xl">
          项目
        </h1>
        <p class="mt-4 text-stone-600">
          数据来自
          <code class="rounded bg-stone-100 px-1 text-sm">GET /portfolio/app/project/list</code>
        </p>
        <p v-if="loading" class="mt-8 text-stone-600">加载中…</p>
        <p v-else-if="error" class="mt-8 text-red-800">{{ error }}</p>
        <ul
          v-else
          class="mt-10 divide-y divide-stone-200 border-y border-stone-200 bg-white/80 shadow-sm md:rounded-xl md:border"
        >
          <li
            v-for="p in projects"
            :key="p.id"
            class="px-4 py-7 md:px-6 md:py-8"
          >
            <div class="flex flex-col gap-2 sm:flex-row sm:items-baseline sm:justify-between">
              <RouterLink
                v-if="p.id != null"
                :to="`/projects/${p.id}`"
                :class="titleClass"
              >
                {{ p.name }}
              </RouterLink>
              <time
                class="shrink-0 text-sm tabular-nums text-stone-500"
                :datetime="p.createTime"
              >
                {{ toYmd(p.createTime) }}
              </time>
            </div>
            <p class="mt-2 text-sm text-stone-600">
              {{ (p.description ?? '').slice(0, 200) }}
            </p>
          </li>
        </ul>
      </div>
    </main>
  </div>
</template>
