import type { PortfolioProjectVo } from './types/backend'
import {
  assertRuoYiR,
  assertRuoYiTable,
  httpJson,
  unwrapR,
  unwrapTable,
} from './http'

function qs(params: Record<string, string | number | undefined>): string {
  const e = new URLSearchParams()
  for (const [k, v] of Object.entries(params)) {
    if (v === undefined || v === '') continue
    e.set(k, String(v))
  }
  const s = e.toString()
  return s ? `?${s}` : ''
}

export async function fetchPortfolioProjectPage(params: {
  pageNum?: number
  pageSize?: number
  name?: string
  techStack?: string
}): Promise<{ total: number; rows: PortfolioProjectVo[] }> {
  const body = await httpJson<unknown>(
    `/portfolio/app/project/list${qs({
      pageNum: params.pageNum ?? 1,
      pageSize: params.pageSize ?? 20,
      name: params.name,
      techStack: params.techStack,
    })}`
  )
  const t = assertRuoYiTable<PortfolioProjectVo>(body)
  return unwrapTable(t)
}

export async function fetchPortfolioProjectDetail(
  id: number
): Promise<PortfolioProjectVo | null> {
  const body = await httpJson<unknown>(`/portfolio/app/project/${id}`)
  const r = assertRuoYiR<PortfolioProjectVo | null>(body)
  if (r.code === 200) return r.data ?? null
  if (r.msg?.includes('不存在')) return null
  throw new Error(r.msg || '加载失败')
}

export async function fetchPortfolioHot(
  limit: number
): Promise<PortfolioProjectVo[]> {
  const body = await httpJson<unknown>(
    `/portfolio/app/project/hot${qs({ limit })}`
  )
  const r = assertRuoYiR<PortfolioProjectVo[]>(body)
  return unwrapR(r)
}

export async function fetchPortfolioRecent(
  limit: number
): Promise<PortfolioProjectVo[]> {
  const body = await httpJson<unknown>(
    `/portfolio/app/project/recent${qs({ limit })}`
  )
  const r = assertRuoYiR<PortfolioProjectVo[]>(body)
  return unwrapR(r)
}

export async function searchPortfolioByName(
  keyword: string,
  limit: number
): Promise<PortfolioProjectVo[]> {
  const body = await httpJson<unknown>(
    `/portfolio/app/project/search${qs({ keyword, limit })}`
  )
  const r = assertRuoYiR<PortfolioProjectVo[]>(body)
  return unwrapR(r)
}

export async function likePortfolioProject(id: number): Promise<number> {
  const body = await httpJson<unknown>(`/portfolio/app/project/${id}/like`, {
    method: 'POST',
  })
  const r = assertRuoYiR<number>(body)
  return unwrapR(r)
}
