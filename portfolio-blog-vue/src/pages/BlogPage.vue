<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import BlogModule from '../blog/BlogModule.vue'
import {
  mockBlogFooterLinks,
  mockBrand,
  mockIndexLinks,
  mockIntro,
  mockNav,
  mockSocial,
} from '../blog/mock/blogMock'
import type { BlogPost } from '../blog/types'
import { articleVoToBlogPost } from '../api/adapters'
import { fetchPublishedArticlePage } from '../api/blogApi'

const route = useRoute()
const posts = ref<BlogPost[]>([])
const loading = ref(true)
const loadError = ref<string | null>(null)

async function loadList() {
  loading.value = true
  loadError.value = null
  try {
    const tagIdRaw = route.query.tagId
    const categoryIdRaw = route.query.categoryId
    const tagId =
      tagIdRaw != null && String(tagIdRaw) !== '' ?
        Number(tagIdRaw)
      : undefined
    const categoryId =
      categoryIdRaw != null && String(categoryIdRaw) !== '' ?
        Number(categoryIdRaw)
      : undefined
    const { rows } = await fetchPublishedArticlePage({
      pageNum: 1,
      pageSize: 100,
      tagId: Number.isFinite(tagId) ? tagId : undefined,
      categoryId: Number.isFinite(categoryId) ? categoryId : undefined,
    })
    posts.value = rows.map(articleVoToBlogPost)
  } catch (e) {
    posts.value = []
    loadError.value =
      e instanceof Error ? e.message : '无法加载文章列表，请确认后端已启动且代理正常'
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
watch(() => [route.query.tagId, route.query.categoryId], loadList)

const statusBanner = computed(() => {
  if (loading.value) return '正在加载文章列表…'
  if (loadError.value) return loadError.value
  return undefined
})

const statusTone = computed(() => (loadError.value ? 'error' : 'neutral'))
</script>

<template>
  <BlogModule
    :brand="mockBrand"
    :nav-items="mockNav"
    :social-links="mockSocial"
    :intro-title="mockIntro.title"
    :intro-description="mockIntro.description"
    :index-links="mockIndexLinks"
    :posts="posts"
    :footer-links="mockBlogFooterLinks"
    :timeline-entries="[]"
    :resolve-post-href="(p) => `/post/${p.id}`"
    :status-banner="statusBanner"
    :status-banner-tone="statusTone"
  />
</template>
