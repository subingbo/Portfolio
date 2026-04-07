<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { BlogPost } from '../types'

const props = withDefaults(
  defineProps<{
    posts: BlogPost[]
    resolvePostHref?: (post: BlogPost) => string
  }>(),
  {
    resolvePostHref: undefined,
  }
)

function hrefFor(post: BlogPost) {
  return props.resolvePostHref?.(post) ?? `/post/${post.id}`
}

const titleClass =
  'font-serif text-base font-semibold text-emerald-950 transition hover:text-emerald-800'
</script>

<template>
  <nav aria-label="文章列表">
    <ul class="m-0 divide-y divide-stone-200 border-y border-stone-200 bg-white/80 p-0 shadow-sm md:rounded-xl md:border">
      <li
        v-for="post in props.posts"
        :key="post.id"
        class="transition hover:bg-emerald-50/50"
      >
        <div
          class="flex flex-col gap-2 px-4 py-7 sm:flex-row sm:items-baseline sm:justify-between sm:gap-10 sm:py-8 md:px-6"
        >
          <div class="min-w-0">
            <RouterLink :to="hrefFor(post)" :class="titleClass">
              {{ post.title }}
            </RouterLink>
            <p class="mt-2 text-sm text-stone-600">{{ post.summary }}</p>
          </div>
          <time
            class="shrink-0 text-sm tabular-nums text-stone-500"
            :datetime="post.date"
          >
            {{ post.date }}
          </time>
        </div>
      </li>
    </ul>
  </nav>
</template>
