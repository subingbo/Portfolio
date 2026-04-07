<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { TextLink } from '../../types'

defineProps<{
  backTo: string
  backLabel: string
  tags: TextLink[]
}>()

const backClass =
  'text-sm font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 transition hover:text-emerald-900 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]'

const tagClass =
  'font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-2 transition hover:text-emerald-900 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]'
</script>

<template>
  <footer
    class="border-t border-emerald-900/10 pt-12 text-sm text-stone-700 md:pt-14"
  >
    <p>
      <RouterLink :to="backTo" :class="backClass">{{ backLabel }}</RouterLink>
    </p>
    <p class="mt-8 flex flex-wrap items-baseline gap-x-3 gap-y-2">
      <span class="text-stone-600">标签</span>
      <RouterLink
        v-for="t in tags"
        :key="t.label + t.href"
        :to="t.href"
        :class="tagClass"
      >
        {{ t.label }}
      </RouterLink>
    </p>
  </footer>
</template>
