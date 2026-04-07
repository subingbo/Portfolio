<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import BlogHeader from '../blog/components/BlogHeader.vue'
import { mockBrand, mockNav, mockSocial } from '../blog/mock/blogMock'

const route = useRoute()
const heading = computed(
  () => (route.meta.title as string | undefined) ?? '页面'
)
</script>

<template>
  <div class="min-h-screen bg-[#faf8f3] text-stone-800 antialiased">
    <BlogHeader
      :brand="mockBrand"
      :nav-items="mockNav"
      :social-links="mockSocial"
    />
    <main
      class="border-t border-emerald-900/5 px-5 py-16 md:px-8 md:py-20"
    >
      <div class="mx-auto max-w-2xl lg:ml-2">
        <h1 class="font-serif text-3xl font-semibold text-stone-900">
          {{ heading }}
        </h1>
        <p class="mt-6 text-base leading-relaxed text-stone-600">
          静态预览站中的占位路由：尚未接入真实页面，仅用于消除路由警告与保留导航结构。
        </p>
        <p class="mt-10">
          <RouterLink
            to="/"
            class="text-sm font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 transition hover:text-emerald-900 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]"
          >
            返回首页
          </RouterLink>
        </p>
      </div>
    </main>
  </div>
</template>
