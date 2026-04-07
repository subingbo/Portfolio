<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import type { BrandLink, NavItem, TextLink } from '../types'
import { accessToken, setAccessToken } from '../../auth/token'
import { authLogout } from '../../api/authApi'

const props = withDefaults(
  defineProps<{
    brand: BrandLink
    navItems: NavItem[]
    socialLinks: TextLink[]
  }>(),
  {}
)

const route = useRoute()
const router = useRouter()

const navItemsResolved = computed(() => {
  const items = [...props.navItems]
  if (accessToken.value) {
    return items
      .filter((i) => i.href !== '/login' && i.href !== '/register')
      .concat([{ label: '退出', href: '#', action: 'logout' as const }])
  }
  return items
})

async function onLogout() {
  await authLogout().catch(() => {})
  setAccessToken(null)
  await router.push('/')
}

const linkFocus =
  'transition hover:text-emerald-800 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]'

const brandClass =
  'font-serif text-xl font-semibold tracking-tight text-emerald-900 transition hover:text-emerald-950 focus:outline-none focus-visible:rounded-sm focus-visible:ring-2 focus-visible:ring-emerald-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[#faf8f3]'

function isNavActive(item: NavItem) {
  const path = route.path
  if (item.action === 'logout') return false
  if (item.href === '/') return path === '/'
  if (item.href === '/blog') return path === '/blog' || path.startsWith('/post/')
  return path === item.href
}
</script>

<template>
  <header
    class="border-b border-emerald-900/10 bg-[#faf8f3]/90 backdrop-blur-sm"
    data-module="site-header"
  >
    <div
      class="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-4 px-5 py-5 md:px-8"
    >
      <RouterLink :to="props.brand.href" :class="brandClass">
        {{ props.brand.label }}
      </RouterLink>
      <nav aria-label="主导航" class="flex flex-wrap gap-6 text-sm text-stone-600">
        <template v-for="item in navItemsResolved" :key="item.label + item.href">
          <span
            v-if="isNavActive(item)"
            class="font-medium text-emerald-900"
            aria-current="page"
          >
            {{ item.label }}
          </span>
          <button
            v-else-if="item.action === 'logout'"
            type="button"
            :class="linkFocus"
            @click="onLogout"
          >
            {{ item.label }}
          </button>
          <RouterLink v-else :to="item.href" :class="linkFocus">{{ item.label }}</RouterLink>
        </template>
      </nav>
      <div class="flex gap-5 text-sm text-stone-600">
        <a
          v-for="s in props.socialLinks"
          :key="s.label"
          :href="s.href"
          :class="linkFocus"
          target="_blank"
          rel="noreferrer"
        >
          {{ s.label }}
        </a>
      </div>
    </div>
  </header>
</template>
