<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { BlogTimelineEntry, TextLink } from '../types'

const props = withDefaults(
  defineProps<{
    heading: string
    entries: BlogTimelineEntry[]
    footerLink?: TextLink
  }>(),
  {
    footerLink: undefined,
  }
)

const entryTitleClass =
  'text-lg font-semibold text-emerald-950 transition hover:text-emerald-800 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#f3f0e7]'

const footerLinkClass =
  'font-medium text-emerald-800 underline decoration-emerald-800/30 underline-offset-4 transition hover:text-emerald-900 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#f3f0e7]'
</script>

<template>
  <section
    v-if="props.entries.length"
    class="border-t border-emerald-900/5 bg-[#f3f0e7] px-5 py-14 md:px-8 md:py-20"
    aria-labelledby="blog-timeline-heading"
  >
    <div class="mx-auto max-w-6xl">
      <h2
        id="blog-timeline-heading"
        class="mb-12 font-serif text-3xl font-semibold text-stone-900 md:mb-14"
      >
        {{ props.heading }}
      </h2>
      <ul
        class="m-0 max-w-3xl list-none space-y-14 border-y border-stone-200/80 bg-white/60 py-6 pl-0 pr-0 sm:space-y-16 md:rounded-xl md:border md:px-8 md:py-8 md:shadow-sm lg:ml-4"
      >
        <li
          v-for="entry in props.entries"
          :key="entry.id"
          class="grid gap-4 sm:grid-cols-[7rem_1fr] sm:gap-x-10"
        >
          <time
            class="text-sm tabular-nums text-stone-500 sm:pt-1 sm:text-right"
            :datetime="entry.dateTime"
          >
            {{ entry.dateLabel }}
          </time>
          <div class="relative border-l border-emerald-800/20 pl-6 sm:pl-8">
            <span
              class="absolute -left-px top-2 size-2 -translate-x-1/2 rounded-full bg-emerald-600 sm:top-2.5"
              aria-hidden="true"
            />
            <RouterLink :to="entry.href" :class="entryTitleClass">
              {{ entry.title }}
            </RouterLink>
            <p class="mt-3 text-sm leading-relaxed text-stone-600 sm:text-base">
              {{ entry.summary }}
            </p>
          </div>
        </li>
      </ul>
      <p v-if="props.footerLink" class="mt-10 text-sm text-stone-600 lg:ml-4">
        <RouterLink :to="props.footerLink.href" :class="footerLinkClass">
          {{ props.footerLink.label }}
        </RouterLink>
      </p>
    </div>
  </section>
</template>
