import { computed, ref } from 'vue'

const LS_KEY = 'ruoyi_access_token'

/** 与 RuoYi Sa-Token 写入的 Bearer 配套，仅存 access_token 字符串 */
export const accessToken = ref<string | null>(localStorage.getItem(LS_KEY))

export const isLoggedIn = computed(() => !!accessToken.value)

export function setAccessToken(token: string | null) {
  accessToken.value = token
  if (token) localStorage.setItem(LS_KEY, token)
  else localStorage.removeItem(LS_KEY)
}
