<script setup lang="ts">
const props = defineProps<{
  title: string
  date: string
  readTime?: string
  /** 例如：阅读 120 · 赞 15 */
  statsLine?: string
}>()
</script>

<template>
  <header class="space-y-8">
    <h1
      class="font-serif text-5xl font-semibold leading-[1.08] tracking-tight text-stone-900 md:text-6xl"
    >
      {{ props.title }}
    </h1>
    <div
      class="flex flex-wrap gap-x-4 gap-y-2 text-sm tabular-nums text-stone-600"
    >
      <time :datetime="props.date">{{ props.date }}</time>
      <template v-if="props.readTime">
        <span class="text-stone-400" aria-hidden="true">·</span>
        <span>{{ props.readTime }}</span>
      </template>
      <template v-if="props.statsLine">
        <span class="text-stone-400" aria-hidden="true">·</span>
        <span>{{ props.statsLine }}</span>
      </template>
    </div>
  </header>
</template>
