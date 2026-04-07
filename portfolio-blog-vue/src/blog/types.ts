export interface BlogPost {
  id: string
  title: string
  date: string
  summary: string
}

export interface TextLink {
  label: string
  href: string
}

export interface NavItem extends TextLink {
  current?: boolean
  /** 特殊操作：退出登录（不导航） */
  action?: 'logout'
}

export interface BrandLink {
  label: string
  href: string
}

export interface BlogTimelineEntry {
  id: string
  dateLabel: string
  dateTime: string
  title: string
  summary: string
  href: string
}

export interface BlogPostBody {
  readTime?: string
  tags: TextLink[]
  /** 正文：每项一段，不含 HTML */
  content: string[]
  /** 可选：将这些下标的段落渲染为文内小标题（h2） */
  subheadingIndexes?: number[]
}

export type BlogPostDetail = BlogPost &
  BlogPostBody & {
    /** 后端浏览量（详情接口） */
    viewCount?: number
    /** 后端点赞数（Redis） */
    likeCount?: number
  }
