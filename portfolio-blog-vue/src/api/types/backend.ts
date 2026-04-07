/** 与后端 BlogArticleVo 对齐（按需扩展） */
export interface BlogArticleVo {
  id?: number
  title?: string
  content?: string
  summary?: string
  cover?: string
  categoryId?: number
  status?: number
  viewCount?: number
  likeCount?: number
  createTime?: string
  updateTime?: string
  categoryName?: string
  tagIds?: number[]
  tags?: BlogTagVo[]
}

export interface BlogTagVo {
  id?: number
  name?: string
}

/** 与 PortfolioProjectVo 对齐 */
export interface PortfolioProjectVo {
  id?: number
  name?: string
  description?: string
  techStack?: string
  githubUrl?: string
  demoUrl?: string
  coverImage?: string
  viewCount?: number
  likeCount?: number
  createTime?: string
  updateTime?: string
}
