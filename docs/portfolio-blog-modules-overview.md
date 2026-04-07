# Portfolio 与 Blog 业务模块概述

本文档说明 `ruoyi-modules` 下 **ruoyi-portfolio（作品集）** 与 **ruoyi-blog（博客）** 的职责划分、对外 HTTP 接口约定及与 RuoYi-Vue-Plus 主工程的集成要点。

## 一、整体定位

| 模块 | 包路径 | 作用 |
|------|--------|------|
| ruoyi-portfolio | `org.dromara.portfolio` | 个人/团队作品集项目维护与公开展示（列表、详情、热门、搜索等） |
| ruoyi-blog | `org.dromara.blog` | 博客文章、分类、标签的后台管理与前台只读展示，以及前台评论 |

两者均依赖 RuoYi 公共模块（Web、MyBatis-Plus、安全、日志等），使用 **Sa-Token** 做登录与接口级权限；公开读接口通过 `@SaIgnore` 声明，并在网关/安全配置中列入匿名白名单。

## 二、安全与匿名放行

在 `ruoyi-admin` 的 `application.yml` 中，`security.excludes` 已配置：

- `/portfolio/app/**`：作品集 **前台** 接口，无需登录。
- `/blog/front/**`：博客 **前台** 接口（文章、分类、标签、评论），无需登录。

后台接口路径为：

- `/portfolio/project/**`：需 `portfolio:project:*` 等系列权限。
- `/blog/admin/**`：需 `blog:article:*`、`blog:category:*`、`blog:tag:*` 等权限。

## 三、ruoyi-portfolio 接口一览

**基础路径（后台）**：`/portfolio/project`

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/list` | portfolio:project:list | 分页查询项目（Query：`PortfolioProjectBo` + `PageQuery`） |
| GET | `/{id}` | portfolio:project:query | 单条详情（管理端，不计公开浏览策略） |
| POST | `/` | portfolio:project:add | 新增（防重复提交 + 操作日志） |
| PUT | `/` | portfolio:project:edit | 修改（校验分组 `EditGroup`） |
| DELETE | `/{ids}` | portfolio:project:remove | 批量删除（路径参数为多个 id） |

**基础路径（前台，匿名）**：`/portfolio/app/project`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 分页列表，与后台共用查询服务但无需权限 |
| GET | `/{id}` | 详情；不存在时返回业务失败提示 |
| GET | `/hot` | 按浏览量热门 Top N，`limit` 默认 10 |
| GET | `/recent` | 最近发布 Top N |
| GET | `/search` | 按名称模糊搜索，`keyword` 必填，`limit` 默认 10 |
| POST | `/{id}/like` | 点赞；同一客户端 IP 在 Redis TTL 内仅首次有效，`R.data` 为当前总赞数 |

**实现要点（服务层）**：详情可走 Redis 缓存、防缓存击穿与异步浏览量统计；管理端详情避免影响前台计数策略。**点赞数**仅存 Redis（`portfolio:project:like:*`），列表/详情 VO 的 `likeCount` 为实时合并字段。上述 GET/POST 均经 **Redis 滑动窗口** 按「端点 + IP」限流（默认约 1 分钟 120 次，见 `PortfolioConstants`）。实体表在多租户配置中通常对 `portfolio_project` 排除租户行拦截，与业务表设计一致。

## 四、ruoyi-blog 接口一览

### 4.1 后台（需权限）

**文章** `/blog/admin/article`

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/list` | blog:article:list | 分页（含草稿等全部状态） |
| GET | `/{id}` | blog:article:query | 后台查看单篇 |
| POST | `/` | blog:article:add | 新增 |
| PUT | `/` | blog:article:edit | 修改 |
| DELETE | `/{ids}` | blog:article:remove | 批量删除 |

**分类** `/blog/admin/category` — 权限前缀 `blog:category:*`，结构与文章类似（list / {id} / POST / PUT / DELETE）。

**标签** `/blog/admin/tag` — 权限前缀 `blog:tag:*`，结构同上。

### 4.2 前台（匿名）

**文章** `/blog/front/article`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 仅已发布；可按 `title`、`categoryId`、`tagId` 筛选 + 分页 |
| GET | `/hot` | 按浏览量，条数受常量上限上限约束 |
| GET | `/latest` | 按创建时间最新 |
| POST | `/{id}/like` | 点赞已发布文章；同一 IP 在 TTL 内仅首次有效，返回当前总赞数 |
| GET | `/{id}` | 详情；未发布或不存在时返回失败文案；已发布正文走 Redis 详情缓存（浏览量仍回写 DB） |

**Redis 说明（文章）**：前台详情缓存键 `blog:article:front:detail:{id}`（缓存体不含实时 `viewCount`，命中后 `incr` 再读回）；点赞计数 `blog:article:like:count:{id}`，去重键 `blog:article:like:voter:*`。列表/详情/热门/最新/点赞等接口均带 **滑动窗口限流**（`BlogConstants` 中 `FRONT_SLIDING_*`）。

**分类** `GET /blog/front/category/list` — 返回前台用分类列表（排序规则由服务层定义）。

**标签** `GET /blog/front/tag/list` — 返回全部标签供筛选或云展示。

**评论** `/blog/front/comment`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 发表评论（防重复提交） |
| GET | `/list` | 按 `articleId` 分页拉取评论 |

## 五、统一响应与分页

- 单对象/操作结果：一般为 `R<T>`（`R.ok` / `R.fail`）。
- 分页列表：`TableDataInfo<T>`，与 `PageQuery` 配套。
- 删除、新增、修改等写操作在 Controller 层常通过基类 `toAjax(...)` 转为统一结果。

具体字段以各 `Vo` / `Bo` 类及通用模块定义为准。

## 六、数据表与多租户

博客与作品集相关表（如 `blog_article`、`blog_category`、`blog_tag`、`blog_article_tag`、`blog_comment`、`portfolio_project`）在 `tenant.excludes` 中列出时，表示 **不启用租户行级插件**，便于单站或与主站租户策略对齐；若后续改为多租户隔离，需同步调整配置与数据。

## 七、代码阅读入口

- **作品集**：`PortfolioProjectController`、`PortfolioAppController`、`IPortfolioProjectService` 及 `PortfolioProjectServiceImpl`。
- **博客**：`BlogAdmin*Controller`、`BlogFront*Controller` 及对应 `IBlog*Service` 实现类。

更细的参数与返回值说明见各接口与控制类上的 **JavaDoc**。

## 八、前端工程对接

与 **`portfolio-blog-vue`** 的接口映射、环境变量、未接线能力清单见：[portfolio-blog-vue-backend-integration.md](./portfolio-blog-vue-backend-integration.md)。
