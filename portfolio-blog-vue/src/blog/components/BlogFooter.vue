<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { TextLink } from '../types'

const props = defineProps<{
  links: TextLink[]
}>()

const linkClass =
  'font-medium text-emerald-800 transition hover:text-emerald-900'
</script>

<template>
  <footer class="mt-16 border-t border-emerald-900/10 pt-10 text-sm text-stone-600 md:mt-20">
    <template v-for="(link, i) in props.links" :key="link.href">
      <RouterLink :to="link.href" :class="linkClass">{{ link.label }}</RouterLink>
      <span
        v-if="i < props.links.length - 1"
        class="mx-2 text-stone-400"
        aria-hidden="true"
      >
        ·
      </span>
    </template>
  </footer>
</template>
