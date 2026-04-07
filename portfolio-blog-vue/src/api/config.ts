/** 接口根路径：开发默认 /api（配合 Vite 代理），生产可为完整 origin 或空（同域） */
export function getApiBase(): string {
  const raw = import.meta.env.VITE_API_BASE
  if (raw === undefined || raw === '') return ''
  return raw.replace(/\/$/, '')
}

export function apiUrl(path: string): string {
  const base = getApiBase()
  const p = path.startsWith('/') ? path : `/${path}`
  return `${base}${p}`
}
