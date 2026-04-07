<script setup lang="ts">
const props = defineProps<{
  content: string[]
  subheadingIndexes?: readonly number[]
}>()

function isSubheading(i: number) {
  return props.subheadingIndexes?.includes(i) ?? false
}
</script>

<template>
  <div class="space-y-12 text-[17px] leading-[1.8] [word-break:break-word]">
    <template v-for="(para, i) in props.content" :key="i">
      <h2
        v-if="isSubheading(i)"
        class="font-serif text-2xl font-semibold tracking-tight text-stone-900"
      >
        {{ para }}
      </h2>
      <p v-else class="text-stone-700">
        {{ para }}
      </p>
    </template>
  </div>
</template>
