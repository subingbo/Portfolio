<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import { mockBrand, mockNav, mockSocial } from '../blog/mock/blogMock'
import {
  fetchPortfolioProjectDetail,
  likePortfolioProject,
} from '../api/portfolioApi'
import type { PortfolioProjectVo } from '../api/types/backend'
import { toYmd } from '../api/adapters'

const route = useRoute()
const id = computed(() => route.params.id as string)
const project = ref<PortfolioProjectVo | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const likeBusy = ref(false)

const linkClass =
  'font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 hover:text-emerald-900'

async function load() {
  loading.value = true
  error.value = null
  const n = Number(id.value)
  if (!Number.isFinite(n)) {
    project.value = null
    loading.value = false
    return
  }
  try {
    project.value = await fetchPortfolioProjectDetail(n)
  } catch (e) {
    project.value = null
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

async function onLike() {
  const n = Number(id.value)
  if (!Number.isFinite(n) || likeBusy.value) return
  likeBusy.value = true
  try {
    await likePortfolioProject(n)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '点赞失败'
  } finally {
    likeBusy.value = false
  }
}

onMounted(load)
watch(id, load)
</script>

<template>
  <div class="min-h-screen bg-[#faf8f3] text-stone-800 antialiased">
    <BlogHeader
      :brand="mockBrand"
      :nav-items="mockNav"
      :social-links="mockSocial"
    />
    <main class="border-t border-emerald-900/5 px-5 py-14 md:px-8 md:py-20">
      <div class="mx-auto max-w-2xl space-y-8 lg:ml-4">
        <nav class="text-sm text-stone-600" aria-label="面包屑">
          <RouterLink to="/projects" :class="linkClass">项目</RouterLink>
          <span class="mx-2 text-stone-400">/</span>
          <span>{{ project?.name ?? '…' }}</span>
        </nav>

        <p v-if="loading">加载中…</p>
        <p v-else-if="error" class="text-red-800">{{ error }}</p>

        <template v-else-if="project">
          <h1 class="font-serif text-4xl font-semibold text-stone-900 md:text-5xl">
            {{ project.name }}
          </h1>
          <p class="text-sm tabular-nums text-stone-500">
            {{ toYmd(project.createTime) }}
            <template v-if="project.viewCount != null">
              · 阅读 {{ project.viewCount }}
            </template>
            <template v-if="project.likeCount != null">
              · 赞 {{ project.likeCount }}
            </template>
          </p>
          <div class="space-y-4 text-[17px] leading-relaxed text-stone-700">
            <p v-for="(para, i) in (project.description ?? '').split(/\n\s*\n/).filter(Boolean)" :key="i">
              {{ para }}
            </p>
          </div>
          <p v-if="project.techStack" class="text-sm text-stone-600">
            技术栈：{{ project.techStack }}
          </p>
          <p class="flex flex-wrap gap-4 text-sm">
            <a
              v-if="project.githubUrl"
              :href="project.githubUrl"
              target="_blank"
              rel="noopener noreferrer"
              :class="linkClass"
            >
              GitHub
            </a>
            <a
              v-if="project.demoUrl"
              :href="project.demoUrl"
              target="_blank"
              rel="noopener noreferrer"
              :class="linkClass"
            >
              Demo
            </a>
          </p>
          <p>
            <button
              type="button"
              class="text-sm font-medium text-emerald-800 underline decoration-emerald-800/30 disabled:opacity-50"
              :disabled="likeBusy"
              @click="onLike"
            >
              {{ likeBusy ? '提交中…' : '点赞' }}
            </button>
          </p>
        </template>

        <p v-else class="text-stone-700">未找到该项目。</p>

        <p>
          <RouterLink to="/projects" :class="linkClass">← 返回项目列表</RouterLink>
        </p>
      </div>
    </main>
  </div>
</template>
