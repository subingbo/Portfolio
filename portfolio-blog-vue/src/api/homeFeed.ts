import type { BlogTimelineEntry } from '../blog/types'
import { fetchPublishedLatest } from './blogApi'
import { fetchPortfolioRecent } from './portfolioApi'
import { articleVoToTimelineEntry, portfolioVoToTimelineEntry } from './adapters'

function parseTime(e: BlogTimelineEntry): number {
  const t = Date.parse(e.dateTime)
  return Number.isNaN(t) ? 0 : t
}

/** 首页时间轴：项目 + 文章按时间倒序合并 */
export async function fetchHomeTimeline(
  articleLimit = 6,
  projectLimit = 6
): Promise<BlogTimelineEntry[]> {
  const [articles, projects] = await Promise.all([
    fetchPublishedLatest(articleLimit),
    fetchPortfolioRecent(projectLimit),
  ])
  const merged = [
    ...projects.map(portfolioVoToTimelineEntry),
    ...articles.map((vo) => articleVoToTimelineEntry(vo)),
  ]
  merged.sort((a, b) => parseTime(b) - parseTime(a))
  return merged.slice(0, articleLimit + projectLimit)
}
