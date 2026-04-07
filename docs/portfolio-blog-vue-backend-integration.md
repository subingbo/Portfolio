# portfolio-blog-vue ↔ ruoyi-blog / ruoyi-portfolio 工程对接说明

本文档描述 **`portfolio-blog-vue` 前端工程** 与 **`ruoyi-modules/ruoyi-blog`、`ruoyi-modules/ruoyi-portfolio`** 的接口级对接方式、数据约定，以及**前端已用 / 未用**与**后端已提供 / 无对应页面**的差异清单，便于联调与迭代。

---

## 1. 工程与运行约定

| 项目 | 说明 |
|------|------|
| 前端 | `portfolio-blog-vue`（Vite + Vue 3 + Vue Router） |
| 后端 | RuoYi 主工程（默认 `http://localhost:8080`，`context-path: /`） |
| 开发代理 | `vite.config.ts`：`/api` → `http://localhost:8080`，并 **strip** `/api` 前缀 |
| 前端环境变量 | `.env.development`：`VITE_API_BASE=/api`；生产见 `.env.production` |

开发时请先启动 **RuoYi**，再执行 `npm run dev`。前端请求形态为：`/api/blog/front/...` → 代理后 **`/blog/front/...`**。

**生产部署**：若前端与 API **不同域**，需配置 `VITE_API_BASE` 为网关完整地址，并在网关或 Spring 侧配置 **CORS**；若由 Nginx **同域反代** API，可将 `VITE_API_BASE` 设为 `''`，使请求走相对路径 `/blog/...`、`/portfolio/...`。

---

## 2. 认证（RuoYi 原生 `/auth`）

| 前端 | 方法 | 后端路径 | 说明 |
|------|------|----------|------|
| `authApi.fetchCaptcha` | GET | `/auth/code` | 验证码；`captchaEnabled=false` 时不展示 |
| `authApi.loginByPassword` | POST | `/auth/login` | 密码模式，body 与官方一致（`clientId`、`grantType=password`、`tenantId`、`username`、`password`、可选 `code`/`uuid`） |
| `authApi.registerUser` | POST | `/auth/register` | `RegisterBody`：`userType` 固定传 `sys_user`；需后台开启注册 |
| `authApi.authLogout` | POST | `/auth/logout` | Header：`Authorization: Bearer {access_token}` |

**传输加密（`api-decrypt`）**：默认 `VITE_AUTH_ENCRYPT` 非 `false` 时，登录/注册 POST 按 RuoYi 约定：请求头 `encrypt-key`（RSA 加密的 Base64(AES 密钥)）+ AES/ECB/PKCS7 密文 JSON。公钥为 `VITE_AUTH_RSA_PUBLIC_KEY`，须与 `ruoyi-admin` 的 `application.yml` → `api-decrypt.publicKey` 一致。若本地关闭后端 **`api-decrypt.enabled`**，请在前端 `.env` 设置 **`VITE_AUTH_ENCRYPT=false`**，此时请求体为明文 JSON。

**默认环境**：`VITE_RUOYI_CLIENT_ID`、`VITE_RUOYI_TENANT_ID` 与脚本中 `sys_client` / 默认租户 `000000` 对齐；多租户请按环境修改。

登录成功后 `access_token` 写入 `localStorage`（`ruoyi_access_token`），导航栏在无 Token 时显示「登录/注册」，有 Token 时显示「退出」并调用 `/auth/logout`。

---

## 3. 后端响应格式（前端解析逻辑）

| 形态 | 典型接口 | 前端处理 |
|------|----------|----------|
| `TableDataInfo<T>` | 分页列表：`total`、`rows`、`code`、`msg` | `src/api/http.ts` → `assertRuoYiTable` + `unwrapTable`（要求 `code === 200`） |
| `R<T>` | 详情、热门列表、点赞返回值 | `assertRuoYiR` + `unwrapR`；业务失败多为 HTTP 200 但 `code !== 200` |

全局 **业务异常**（如滑动窗口限流）通常仍为 HTTP 200，body 为 `R` 且 `code` 非 200，`msg` 为原因。

---

## 4. 已对接接口一览（前端真实调用）

### 4.1 博客 `ruoyi-blog`（仅前台匿名接口）

| 前端使用位置 | 方法 | 后端路径 | 说明 |
|--------------|------|----------|------|
| `blogApi.fetchPublishedArticlePage` | GET | `/blog/front/article/list` | `pageNum`/`pageSize`/`title`/`categoryId`/`tagId`；博客列表页、筛标签 |
| `blogApi.fetchPublishedArticleDetail` | GET | `/blog/front/article/{id}` | 文章详情页 |
| `blogApi.likePublishedArticle` | POST | `/blog/front/article/{id}/like` | 详情页点赞 |
| `blogApi.fetchPublishedHot` | GET | `/blog/front/article/hot` | *当前前端未调用*（仅 `homeFeed` 用 latest，见 §5） |
| `blogApi.fetchPublishedLatest` | GET | `/blog/front/article/latest` | 首页时间轴混排 |

**未接博客接口（后端已有）**：

- `GET /blog/front/category/list`
- `GET /blog/front/tag/list`
- `POST /blog/front/comment`、`GET /blog/front/comment/list`
- 全部 `/blog/admin/**`（管理后台，本站静态站不对接）

### 4.2 作品集 `ruoyi-portfolio`（仅 App 匿名接口）

| 前端使用位置 | 方法 | 后端路径 | 说明 |
|--------------|------|----------|------|
| `portfolioApi.fetchPortfolioProjectPage` | GET | `/portfolio/app/project/list` | 项目列表页 |
| `portfolioApi.fetchPortfolioProjectDetail` | GET | `/portfolio/app/project/{id}` | 项目详情页 |
| `portfolioApi.fetchPortfolioRecent` | GET | `/portfolio/app/project/recent` | 首页时间轴混排 |
| `portfolioApi.likePortfolioProject` | POST | `/portfolio/app/project/{id}/like` | 项目详情点赞 |
| `portfolioApi.fetchPortfolioHot` | GET | `/portfolio/app/project/hot` | *封装已实现，页面未调用* |
| `portfolioApi.searchPortfolioByName` | GET | `/portfolio/app/project/search` | *封装已实现，页面未调用* |

**未接作品集接口（后端已有）**：`/portfolio/project/**` 管理端。

---

## 5. 前端路由 vs 后端能力

| 前端路由 | 行为 | 后端依赖 |
|----------|------|----------|
| `/` | 首页 + 时间轴（文章 latest + 项目 recent 合并排序） | §3.1 latest + §3.2 recent |
| `/blog` | 文章列表；支持 `?tagId=`、`?categoryId=` | list |
| `/post/:id` | 文章详情 + 点赞 | detail + like |
| `/projects` | 项目列表 | project list |
| `/projects/:id` | 项目详情 + 点赞 | project detail + like |
| `/tags`、`/search`、`/profile`、`/login`、`/recent-comments` | 仍占位页 | **无对应专用聚合接口**（搜索可用后端 list/search 扩展，当前未做） |

**结构差异（曾用 mock）**：

- 原 mock 时间轴中项目链接可为 **slug**（如 `/projects/ticket-orchestration`）；现统一为 **数字 id**：`/projects/{id}`。
- 文章详情正文为后端 **Markdown 字符串**；前端按换行拆成段落展示，**不做 Markdown 渲染**（与旧 mock 的「纯文本段」一致）。

---

## 6. 字段与产品层差异

| 字段 | 后端 | 前端展示 |
|------|------|----------|
| `createTime` | `Date` → JSON 一般为 ISO 字符串 | 摘要行取 `yyyy-MM-dd` |
| `summary` | 可有 | 列表摘要；无则用标签/分类拼一行 |
| `content` | Markdown | 按 `\n\n` 或 `\n` 拆段 |
| `tags` | `BlogTagVo[]` | 标签链到 `/blog?tagId=` |
| `viewCount` / `likeCount` | 详情含浏览与 Redis 赞 | 文章详情页副标题；列表可不展示 |
| `likeCount`（项目） | Redis | 详情页文案行 |

---

## 7. 限流与错误处理

后端对匿名 **博客文章**、**作品集 App** 部分接口使用 **Redis 滑动窗口**（按 IP + 端点）。短时间大量刷新可能返回业务错误；前端以文案展示 `msg` 或 Error message。

---

## 8. 代码入口索引（便于改需求）

| 职责 | 路径 |
|------|------|
| API 基础地址 | `portfolio-blog-vue/src/api/config.ts` |
| HTTP + R/Table 解析 | `portfolio-blog-vue/src/api/http.ts` |
| 认证 / 加密 | `src/api/authApi.ts`、`src/api/ruoyiCrypto.ts`、`src/auth/token.ts` |
| 博客/作品集请求 | `portfolio-blog-vue/src/api/blogApi.ts`、`portfolioApi.ts` |
| VO → 页面模型 | `portfolio-blog-vue/src/api/adapters.ts`、`homeFeed.ts` |
| 页面 | `HomePage`、`BlogPage`、`PostDetailPage`、`ProjectsPage`、`ProjectDetailPage`、`LoginPage`、`RegisterPage` |

---

## 9. 小结：缺漏与多出对照

### 9.1 后端有 · 前端尚未接线（封装可能已写）

- 博客：`GET .../hot`，分类/标签列表，评论提交/列表。
- 作品集：`GET .../hot`，`GET .../search`（`blogApi`/`portfolioApi` 中部分方法已封装但页面未用）。

### 9.2 前端有 · 后端无专用页面级接口

- `/tags`、`/search`、`/recent-comments`、`/profile`：仍为占位；搜索可后续对接 `GET /portfolio/app/project/search` 与博客 `list` 的 `title` 条件。
- `/login`、`/register` 已对接 RuoYi **`/auth`**；带 Token 调用 **管理端** `blog/admin/**`、`portfolio/project/**` 尚未在站内封装（`fetch` 需统一加 `Authorization: Bearer`）。

### 9.3 管理端与一体站

- 当前 `portfolio-blog-vue` 为展示站：**前台博客/作品集接口无需 Token**；若要在同一前端内做 **后台维护**，请扩展 `http.ts` 或单独 `adminApi.ts`，并携带 `Authorization: Bearer {access_token}` 调用上述 admin 路径。

---

维护建议：变更后端路径或包装类型时，同步修改 `src/api/*` 与本文档；新增页面时在本表追加一行「前端路由 ↔ 接口」。
