package org.dromara.portfolio.constant;

import java.time.Duration;

/**
 * 作品集模块 Redis / 锁常量
 */
public final class PortfolioConstants {

    private PortfolioConstants() {
    }

    /** 详情缓存 key 前缀：portfolio:project:detail:{id} */
    public static final String REDIS_DETAIL_PREFIX = "portfolio:project:detail:";

    /** 与 Lock4j 配合的防击穿锁名前缀（业务键） */
    public static final String LOCK_DETAIL_LOAD_PREFIX = "portfolio:project:detail:load:";

    /** 详情缓存 TTL */
    public static final Duration DETAIL_CACHE_TTL = Duration.ofMinutes(30);

    /** 分布式锁：等待 5s，租约 30s（与 demo RedisLockController 惯例一致，可按集群调优） */
    public static final long LOCK_ACQUIRE_MS = 5_000L;
    public static final long LOCK_LEASE_MS = 30_000L;

    /** 点赞总数：portfolio:project:like:count:{id} */
    public static final String REDIS_PROJECT_LIKE_COUNT_PREFIX = "portfolio:project:like:count:";

    /** 访客是否已赞：portfolio:project:like:voter:{id}:{voterKey} */
    public static final String REDIS_PROJECT_LIKE_VOTER_PREFIX = "portfolio:project:like:voter:";

    public static final Duration LIKE_VOTER_TTL = Duration.ofDays(30);

    /** 公开接口滑动窗口 key 前缀：portfolio:app:sw:{endpoint}:{ip} */
    public static final String REDIS_APP_SLIDING_PREFIX = "portfolio:app:sw:";

    public static final Duration APP_SLIDING_WINDOW = Duration.ofMinutes(1);

    public static final int APP_SLIDING_MAX = 120;
}
