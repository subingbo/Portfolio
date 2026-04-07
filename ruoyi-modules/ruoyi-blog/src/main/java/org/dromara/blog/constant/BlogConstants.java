package org.dromara.blog.constant;

import java.time.Duration;

/**
 * 博客模块常量
 *
 * @author ruoyi-blog
 */
public final class BlogConstants {

    private BlogConstants() {
    }

    /** 草稿 */
    public static final int STATUS_DRAFT = 0;

    /** 已发布（前台可见） */
    public static final int STATUS_PUBLISHED = 1;

    /** 前台热门/最新列表默认条数上限 */
    public static final int FRONT_LIST_MAX = 50;

    /** 前台热门/最新默认条数 */
    public static final int FRONT_LIST_DEFAULT = 10;

    // ---------- Redis：前台文章详情缓存（正文+标签等；不含实时 viewCount） ----------

    /** 已发布文章详情缓存：blog:article:front:detail:{id} */
    public static final String REDIS_ARTICLE_FRONT_DETAIL_PREFIX = "blog:article:front:detail:";

    /** 与 Lock4j 配合的防击穿锁名前缀 */
    public static final String LOCK_ARTICLE_DETAIL_LOAD_PREFIX = "blog:article:front:detail:load:";

    public static final Duration ARTICLE_FRONT_DETAIL_TTL = Duration.ofMinutes(30);

    public static final long LOCK_ACQUIRE_MS = 5_000L;

    public static final long LOCK_LEASE_MS = 30_000L;

    // ---------- Redis：点赞（计数 + 访客去重） ----------

    /** 点赞总数：blog:article:like:count:{articleId} */
    public static final String REDIS_ARTICLE_LIKE_COUNT_PREFIX = "blog:article:like:count:";

    /** 访客是否已赞：blog:article:like:voter:{articleId}:{voterKey} */
    public static final String REDIS_ARTICLE_LIKE_VOTER_PREFIX = "blog:article:like:voter:";

    public static final Duration LIKE_VOTER_TTL = Duration.ofDays(30);

    // ---------- Redis：前台查询滑动窗口（按 IP + 端点） ----------

    public static final String REDIS_SLIDING_KEY_PREFIX = "blog:front:sw:";

    public static final Duration FRONT_SLIDING_WINDOW = Duration.ofMinutes(1);

    public static final int FRONT_SLIDING_MAX = 120;
}
