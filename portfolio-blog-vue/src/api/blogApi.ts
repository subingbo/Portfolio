import type { BlogArticleVo } from './types/backend'
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

export async function fetchPublishedArticlePage(params: {
  pageNum?: number
  pageSize?: number
  title?: string
  categoryId?: number
  tagId?: number
}): Promise<{ total: number; rows: BlogArticleVo[] }> {
  const body = await httpJson<unknown>(
    `/blog/front/article/list${qs({
      pageNum: params.pageNum ?? 1,
      pageSize: params.pageSize ?? 10,
      title: params.title,
      categoryId: params.categoryId,
      tagId: params.tagId,
    })}`
  )
  const t = assertRuoYiTable<BlogArticleVo>(body)
  return unwrapTable(t)
}

export async function fetchPublishedArticleDetail(
  id: number
): Promise<BlogArticleVo | null> {
  const body = await httpJson<unknown>(`/blog/front/article/${id}`)
  const r = assertRuoYiR<BlogArticleVo | null>(body)
  if (r.code === 200) return r.data ?? null
  if (r.msg?.includes('不存在') || r.msg?.includes('未发布')) return null
  throw new Error(r.msg || '加载失败')
}

export async function likePublishedArticle(id: number): Promise<number> {
  const body = await httpJson<unknown>(`/blog/front/article/${id}/like`, {
    method: 'POST',
  })
  const r = assertRuoYiR<number>(body)
  return unwrapR(r)
}

export async function fetchPublishedHot(limit: number): Promise<BlogArticleVo[]> {
  const body = await httpJson<unknown>(
    `/blog/front/article/hot${qs({ limit })}`
  )
  const r = assertRuoYiR<BlogArticleVo[]>(body)
  return unwrapR(r)
}

export async function fetchPublishedLatest(
  limit: number
): Promise<BlogArticleVo[]> {
  const body = await httpJson<unknown>(
    `/blog/front/article/latest${qs({ limit })}`
  )
  const r = assertRuoYiR<BlogArticleVo[]>(body)
  return unwrapR(r)
}
