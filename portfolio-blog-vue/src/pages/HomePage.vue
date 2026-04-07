<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import BlogIntro from '../blog/components/BlogIntro.vue'
import BlogTimeline from '../blog/components/BlogTimeline.vue'
import {
  mockBrand,
  mockHomeIndexLinks,
  mockHomeKicker,
  mockHomeParagraphs,
  mockNav,
  mockSocial,
  mockTimeline,
  mockTimelineFooterLink,
} from '../blog/mock/blogMock'
import type { BlogTimelineEntry } from '../blog/types'
import { fetchHomeTimeline } from '../api/homeFeed'

const footerLinkClass =
  'font-medium text-emerald-800 transition hover:text-emerald-900 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]'

const timeline = ref<BlogTimelineEntry[]>([...mockTimeline])
const timelineError = ref<string | null>(null)

onMounted(async () => {
  try {
    timeline.value = await fetchHomeTimeline(6, 6)
    timelineError.value = null
  } catch {
    timeline.value = [...mockTimeline]
    timelineError.value =
      '无法加载线上时间轴，已展示本地占位数据。请启动 RuoYi(8080) 后刷新。'
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

    <section
      class="border-b border-emerald-900/5 bg-[#faf8f3] px-5 py-14 md:px-8 md:py-20"
    >
      <div class="mx-auto max-w-6xl">
        <div class="max-w-3xl lg:ml-2">
          <BlogIntro
            :kicker="mockHomeKicker"
            :paragraphs="mockHomeParagraphs"
            :index-links="mockHomeIndexLinks"
          >
            <template #title>
              接口与 <span class="text-emerald-800">迁移</span> 的边界记录
            </template>
          </BlogIntro>
        </div>
      </div>
    </section>

    <p
      v-if="timelineError"
      class="border-b border-amber-200 bg-amber-50 px-5 py-3 text-sm text-amber-950 md:px-8"
    >
      {{ timelineError }}
    </p>

    <BlogTimeline
      heading="近期条目"
      :entries="timeline"
      :footer-link="mockTimelineFooterLink"
    />

    <footer
      class="border-t border-emerald-900/10 bg-[#faf8f3] px-5 py-10 md:px-8"
      data-module="site-footer"
    >
      <div class="mx-auto max-w-6xl">
        <h2 class="sr-only">联系</h2>
        <p class="text-sm leading-relaxed text-stone-500">
          联系：
          <a
            href="mailto:you@example.dev"
            :class="footerLinkClass"
          >
            you@example.dev
          </a>
          <span class="mx-2 text-stone-400" aria-hidden="true">·</span>
          <RouterLink to="/blog" :class="footerLinkClass">文章索引</RouterLink>
        </p>
      </div>
    </footer>
  </div>
</template>
