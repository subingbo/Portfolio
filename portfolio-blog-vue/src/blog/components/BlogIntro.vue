<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { TextLink } from '../types'

const props = withDefaults(
  defineProps<{
    title?: string
    description?: string
    kicker?: string
    indexLinks?: TextLink[]
    paragraphs?: readonly string[] | string[]
  }>(),
  {
    title: '',
    description: '',
    kicker: undefined,
    indexLinks: undefined,
    paragraphs: undefined,
  }
)

const linkClass =
  'font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 transition hover:text-emerald-900'

const h1Class =
  'font-serif text-5xl font-semibold leading-[1.08] tracking-tight text-stone-900 md:text-6xl'
</script>

<template>
  <header class="mb-12 max-w-3xl md:mb-16 lg:ml-2">
    <p
      v-if="props.kicker"
      class="text-sm font-medium uppercase tracking-[0.2em] text-emerald-700/80"
    >
      {{ props.kicker }}
    </p>
    <h1 v-if="$slots.title" :class="[h1Class, props.kicker ? 'mt-5' : '']">
      <slot name="title" />
    </h1>
    <h1 v-else :class="[h1Class, props.kicker ? 'mt-5' : '']">
      {{ props.title }}
    </h1>

    <div
      v-if="props.paragraphs?.length"
      class="mt-8 space-y-6 text-base leading-relaxed text-stone-600"
    >
      <p v-for="(line, i) in props.paragraphs" :key="i">{{ line }}</p>
    </div>
    <p v-else class="mt-6 text-base leading-relaxed text-stone-600">{{ props.description }}</p>
  </header>

  <div
    v-if="props.indexLinks?.length"
    class="mb-10 flex flex-wrap items-center justify-between gap-6 border-b border-stone-200 pb-8"
  >
    <p class="text-sm text-stone-600">
      <template v-for="(link, i) in props.indexLinks" :key="link.href">
        <RouterLink :to="link.href" :class="linkClass">{{ link.label }}</RouterLink>
        <span
          v-if="i < props.indexLinks!.length - 1"
          class="mx-2 text-stone-400"
          aria-hidden="true"
        >
          ·
        </span>
      </template>
    </p>
  </div>
</template>
