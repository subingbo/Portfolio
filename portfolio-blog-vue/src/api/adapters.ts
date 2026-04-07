import type { BlogPost, BlogPostDetail, BlogTimelineEntry, TextLink } from '../blog/types'
import type { BlogArticleVo, PortfolioProjectVo } from './types/backend'

export function toYmd(value: string | undefined): string {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value.slice(0, 10)
  return d.toISOString().slice(0, 10)
}

/** 将正文拆成段落；优先双换行，否则单换行 */
export function splitContentParagraphs(mdOrText: string | undefined): string[] {
  if (!mdOrText?.trim()) return []
  const byDouble = mdOrText.split(/\n\s*\n/).map((s) => s.trim()).filter(Boolean)
  if (byDouble.length > 1) return byDouble
  return mdOrText.split('\n').map((s) => s.trim()).filter(Boolean)
}

export function tagsToLinks(tags: BlogArticleVo['tags']): TextLink[] {
  if (!tags?.length) return []
  return tags.map((t) => ({
    label: t.name ?? '',
    href: t.id != null ? `/blog?tagId=${t.id}` : '/blog',
  }))
}

export function articleVoToBlogPost(vo: BlogArticleVo): BlogPost {
  const id = vo.id != null ? String(vo.id) : ''
  const date = toYmd(vo.createTime)
  const tagPart =
    vo.tags?.length ?
      `标签：${vo.tags.map((t) => t.name).filter(Boolean).join(' · ')}`
    : ''
  const summary =
    vo.summary?.trim() ?
      vo.summary.trim()
    : tagPart || (vo.categoryName ? `分类：${vo.categoryName}` : '')

  return {
    id,
    title: vo.title ?? '',
    date,
    summary: summary || ' ',
  }
}

export function articleVoToDetail(vo: BlogArticleVo): BlogPostDetail {
  const base = articleVoToBlogPost(vo)
  const parts = splitContentParagraphs(vo.content)
  const readTime =
    parts.length ?
      `约 ${Math.max(1, Math.round(parts.join('').length / 500))} 分钟阅读`
    : undefined

  return {
    ...base,
    readTime,
    tags: tagsToLinks(vo.tags),
    content: parts.length ? parts : ['（暂无正文）'],
    subheadingIndexes: [],
    viewCount: vo.viewCount ?? undefined,
    likeCount: vo.likeCount ?? undefined,
  }
}

export function articleVoToTimelineEntry(
  vo: BlogArticleVo,
  hrefPrefix = '/post'
): BlogTimelineEntry {
  const id = vo.id != null ? String(vo.id) : ''
  const ymd = toYmd(vo.createTime)
  return {
    id: `post-${id}`,
    dateLabel: ymd,
    dateTime: vo.createTime ?? ymd,
    title: vo.title ?? '',
    summary: vo.summary ?? '',
    href: `${hrefPrefix}/${id}`,
  }
}

export function portfolioVoToTimelineEntry(
  vo: PortfolioProjectVo
): BlogTimelineEntry {
  const id = vo.id != null ? String(vo.id) : ''
  const ymd = toYmd(vo.createTime)
  return {
    id: `proj-${id}`,
    dateLabel: ymd || '项目',
    dateTime: vo.createTime ?? ymd,
    title: vo.name ?? '',
    summary: (vo.description ?? '').slice(0, 160) || (vo.techStack ?? ''),
    href: `/projects/${id}`,
  }
}
