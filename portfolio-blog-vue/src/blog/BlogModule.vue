<script setup lang="ts">
import BlogHeader from './components/BlogHeader.vue'
import BlogIntro from './components/BlogIntro.vue'
import BlogPostList from './components/BlogPostList.vue'
import BlogTimeline from './components/BlogTimeline.vue'
import BlogFooter from './components/BlogFooter.vue'
import type {
  BlogPost,
  BlogTimelineEntry,
  BrandLink,
  NavItem,
  TextLink,
} from './types'

const props = withDefaults(
  defineProps<{
    brand: BrandLink
    navItems: NavItem[]
    socialLinks: TextLink[]
    introTitle: string
    introDescription: string
    introKicker?: string
    indexLinks?: TextLink[]
    posts: BlogPost[]
    footerLinks: TextLink[]
    timelineHeading?: string
    timelineEntries?: BlogTimelineEntry[]
    timelineFooterLink?: TextLink
    resolvePostHref?: (post: BlogPost) => string
    /** 列表页顶栏提示（加载失败、接口限流等） */
    statusBanner?: string
    statusBannerTone?: 'neutral' | 'error'
  }>(),
  {
    introKicker: undefined,
    indexLinks: undefined,
    timelineHeading: '近期条目',
    timelineEntries: () => [],
    timelineFooterLink: undefined,
    resolvePostHref: undefined,
    statusBanner: undefined,
    statusBannerTone: 'neutral',
  }
)
</script>

<template>
  <div class="min-h-screen bg-[#faf8f3] text-stone-800 antialiased">
    <BlogHeader
      :brand="props.brand"
      :nav-items="props.navItems"
      :social-links="props.socialLinks"
    />

    <p
      v-if="props.statusBanner"
      class="border-b border-emerald-900/10 bg-amber-50/90 px-5 py-3 text-sm text-amber-950 md:px-8"
      :class="
        props.statusBannerTone === 'error' ?
          'border-red-200 bg-red-50 text-red-900'
        : ''
      "
      role="status"
    >
      {{ props.statusBanner }}
    </p>

    <BlogTimeline
      v-if="(props.timelineEntries?.length ?? 0) > 0"
      :heading="props.timelineHeading!"
      :entries="props.timelineEntries!"
      :footer-link="props.timelineFooterLink"
    />

    <div class="border-t border-emerald-900/5 bg-[#eef6f0] px-5 py-14 md:px-8 md:py-20">
      <div class="mx-auto max-w-6xl">
        <BlogIntro
          :kicker="props.introKicker"
          :title="props.introTitle"
          :description="props.introDescription"
          :index-links="props.indexLinks"
        />

        <main data-page="blog-index" class="max-w-3xl lg:ml-4">
          <BlogPostList
            :posts="props.posts"
            :resolve-post-href="props.resolvePostHref"
          />
        </main>

        <BlogFooter :links="props.footerLinks" />
      </div>
    </div>
  </div>
</template>
