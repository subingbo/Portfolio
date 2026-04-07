<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import PostHeader from '../blog/components/post/PostHeader.vue'
import PostContent from '../blog/components/post/PostContent.vue'
import PostFooter from '../blog/components/post/PostFooter.vue'
import { mockBrand, mockNav, mockSocial } from '../blog/mock/blogMock'
import type { BlogPostDetail } from '../blog/types'
import { articleVoToDetail } from '../api/adapters'
import {
  fetchPublishedArticleDetail,
  likePublishedArticle,
} from '../api/blogApi'

const route = useRoute()
const id = computed(() => route.params.id as string)
const post = ref<BlogPostDetail | null>(null)
const loading = ref(true)
const loadError = ref<string | null>(null)
const likeSubmitting = ref(false)

const crumbLink =
  'font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 transition hover:text-emerald-900 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]'

async function loadPost() {
  loading.value = true
  loadError.value = null
  const n = Number(id.value)
  if (!Number.isFinite(n)) {
    post.value = null
    loading.value = false
    return
  }
  try {
    const vo = await fetchPublishedArticleDetail(n)
    post.value = vo ? articleVoToDetail(vo) : null
  } catch (e) {
    post.value = null
    loadError.value =
      e instanceof Error ? e.message : '无法加载文章，请确认后端已启动且代理正常'
  } finally {
    loading.value = false
  }
}

async function onLike() {
  const n = Number(id.value)
  if (!Number.isFinite(n) || likeSubmitting.value) return
  likeSubmitting.value = true
  try {
    await likePublishedArticle(n)
    await loadPost()
  } catch (e) {
    loadError.value =
      e instanceof Error ? e.message : '点赞失败（可能触发了接口限流）'
  } finally {
    likeSubmitting.value = false
  }
}

onMounted(loadPost)
watch(id, loadPost)

const statsLine = computed(() => {
  if (!post.value) return undefined
  const v = post.value.viewCount
  const l = post.value.likeCount
  if (v == null && l == null) return undefined
  const parts: string[] = []
  if (v != null) parts.push(`阅读 ${v}`)
  if (l != null) parts.push(`赞 ${l}`)
  return parts.join(' · ')
})
</script>

<template>
  <div class="min-h-screen bg-[#faf8f3] text-stone-800 antialiased">
    <BlogHeader
      :brand="mockBrand"
      :nav-items="mockNav"
      :social-links="mockSocial"
    />

    <div
      v-if="loading"
      class="border-t border-emerald-900/5 px-5 py-16 md:px-8"
    >
      <p class="mx-auto max-w-2xl text-stone-600 lg:ml-2">正在加载…</p>
    </div>

    <div
      v-else-if="loadError"
      class="border-t border-emerald-900/5 px-5 py-16 md:px-8"
    >
      <p class="mx-auto max-w-2xl text-red-800 lg:ml-2">{{ loadError }}</p>
    </div>

    <template v-else-if="post">
      <div
        class="border-t border-emerald-900/5 bg-[#faf8f3] px-5 py-16 md:px-8 md:py-20"
      >
        <div class="mx-auto max-w-6xl">
          <nav
            class="mb-12 max-w-2xl text-sm text-stone-600 lg:ml-2 md:mb-14"
            aria-label="面包屑"
          >
            <RouterLink to="/blog" :class="crumbLink">文章</RouterLink>
            <span class="mx-2 text-stone-400" aria-hidden="true">/</span>
            <span class="text-stone-600">{{ post.title }}</span>
          </nav>

          <article
            class="max-w-2xl space-y-14 md:space-y-16 lg:ml-2"
            data-page="blog-post"
          >
            <PostHeader
              :title="post.title"
              :date="post.date"
              :read-time="post.readTime"
              :stats-line="statsLine"
            />
            <PostContent
              :content="post.content"
              :subheading-indexes="post.subheadingIndexes"
            />
            <p class="text-sm text-stone-600">
              <button
                type="button"
                class="font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 hover:text-emerald-900 disabled:opacity-50"
                :disabled="likeSubmitting"
                @click="onLike"
              >
                {{ likeSubmitting ? '提交中…' : '点赞' }}
              </button>
            </p>
            <PostFooter
              back-to="/blog"
              back-label="返回文章索引"
              :tags="post.tags"
            />
          </article>
        </div>
      </div>
    </template>

    <div
      v-else
      class="border-t border-emerald-900/5 px-5 py-20 md:px-8"
    >
      <div class="mx-auto max-w-2xl lg:ml-2">
        <p class="text-base text-stone-700">未找到该文章。</p>
        <p class="mt-8">
          <RouterLink to="/blog" :class="crumbLink">返回博客</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>
