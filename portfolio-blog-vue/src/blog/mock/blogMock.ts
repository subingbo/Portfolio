import type {
  BlogPost,
  BlogPostBody,
  BlogPostDetail,
  BlogTimelineEntry,
  NavItem,
  TextLink,
} from '../types'

export const mockBrand = {
  label: '作品集 / 博客',
  href: '/',
} as const

export const mockNav: NavItem[] = [
  { label: '首页', href: '/' },
  { label: '项目', href: '/projects' },
  { label: '博客', href: '/blog' },
  { label: '个人', href: '/profile' },
  { label: '登录', href: '/login' },
  { label: '注册', href: '/register' },
]

export const mockSocial: TextLink[] = [
  { label: 'GitHub', href: 'https://github.com' },
  { label: 'LinkedIn', href: 'https://linkedin.com' },
]

export const mockHomeKicker = '自述 · 占位'

export const mockHomeParagraphs = [
  '本站用于归档可复核的交付说明：项目约束、数据流取舍、失败模式。写作默认读者是六个月后的自己，因此少用形容词，多写前提与不变量。',
  '公开面只放脱敏摘要；细节在私有仓库与审计日志里对齐。若你需要完整上下文，请通过联系方式说明使用场景与时间表。',
] as const

export const mockHomeIndexLinks: TextLink[] = [
  { label: '查看项目索引', href: '/projects' },
  { label: '查看文章索引', href: '/blog' },
]

export const mockIntro = {
  title: '文章',
  description: '列表即索引：标题、日期、可选标签行。不做封面网格。',
} as const

export const mockIndexLinks: TextLink[] = [
  { label: '按标签浏览', href: '/tags' },
  { label: '搜索', href: '/search' },
]

export const mockPosts: BlogPost[] = [
  {
    id: '1',
    title: '迁移脚本的可重复执行条件',
    date: '2026-03-12',
    summary: '标签：迁移 · 数据库 · 运维',
  },
  {
    id: '2',
    title: '错误码分层：何时不暴露内部原因',
    date: '2026-01-28',
    summary: '标签：API · 安全',
  },
  {
    id: '3',
    title: '日志字段最小集与检索成本',
    date: '2025-11-04',
    summary: '标签：可观测性',
  },
  {
    id: '4',
    title: '分页游标与排序稳定性',
    date: '2025-08-19',
    summary: '标签：API · 数据一致',
  },
]

const tag = (label: string): TextLink => ({
  label,
  href: '/tags',
})

export const mockPostBodies: Record<string, BlogPostBody> = {
  '1': {
    readTime: '约 6 分钟阅读（占位）',
    tags: [tag('迁移'), tag('数据库'), tag('运维')],
    subheadingIndexes: [2],
    content: [
      '可重复执行不是「再跑一遍不报错」这么简单，而是：在部分成功、部分失败的中间态下，幂等重试能把系统带到同一终态。前提是把变更拆成可观测的步骤，并且每一步有自己的前置条件。',
      '做法是：每一步执行前检查目标结构/数据是否已满足期望；若已满足则跳过并记录审计；若不满足且无法安全继续，则中止并留下明确错误码，避免静默吞掉数据倾斜。',
      '失败的形状',
      '常见失败是超时后重试：第一次 DDL 已提交，应用层以为失败又发第二次。解决靠「存在性检测 + 版本表」：迁移表记录脚本 ID 与 checksum，只有未执行或 checksum 变更才允许进入危险区。',
      '若生产禁止长事务，则大表改 schema 需要分批与回填任务拆分，这在文章层面只记结论：编排权从单一脚本交给作业队列，脚本书写「最小 DDL」，数据迁移单独度量进度。',
    ],
  },
  '2': {
    readTime: '约 4 分钟阅读（占位）',
    tags: [tag('API'), tag('安全')],
    content: [
      '客户端需要稳定、可操作的错误语义；服务端需要保留足够上下文供排障。分层的目标是：对外暴露枚举化的业务/协议错误，对内记录追踪 ID 与内部原因。',
      '当错误可能暴露实现细节或绕过授权边界时，应在网关或错误映射层做收敛，并把敏感信息限制在受限日志与工单系统内。',
    ],
  },
  '3': {
    readTime: '约 5 分钟阅读（占位）',
    tags: [tag('可观测性')],
    content: [
      '日志字段越少越好，但必须覆盖请求标识、租户/主体、关键业务键与结果状态。缺少其中任意一项，都会在事故复盘时指数级放大检索成本。',
      '索引与采样策略需要与查询路径一致：先定义你会如何发问，再决定字段、基数与保留周期。',
    ],
  },
  '4': {
    readTime: '约 5 分钟阅读（占位）',
    tags: [tag('API'), tag('数据一致')],
    content: [
      '游标分页要求排序键稳定且唯一；否则翻页会出现重复或遗漏。常见修复是为排序列补充主键作为决胜键。',
      '并发写入导致序变化时，客户端应接受短窗口内的重复并以后端去重或业务幂等为准。',
    ],
  },
}

export const mockTimeline: BlogTimelineEntry[] = [
  {
    id: 't1',
    dateLabel: '2024 — Q4',
    dateTime: '2024-10',
    title: '内部工单编排服务',
    summary: '状态机约束 + 幂等写入；对外接口数量刻意保持较少。',
    href: '/projects/ticket-orchestration',
  },
  {
    id: 't2',
    dateLabel: '2026-03-12',
    dateTime: '2026-03-12',
    title: '迁移脚本的可重复执行条件',
    summary: '变更集需自带前置检测；失败时留下可回滚断点。',
    href: '/post/1',
  },
]

export const mockBlogFooterLinks: TextLink[] = [
  { label: '近期评论', href: '/recent-comments' },
  { label: '首页', href: '/' },
]

export const mockTimelineFooterLink: TextLink = {
  label: '全站近期评论',
  href: '/recent-comments',
}

export function getPostDetailById(id: string): BlogPostDetail | null {
  const post = mockPosts.find((p) => p.id === id)
  const body = mockPostBodies[id]
  if (!post || !body) return null
  return { ...post, ...body }
}
