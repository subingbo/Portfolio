package org.dromara.common.redis.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.SpringUtils;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 基于 Redis ZSET + Lua 的滑动窗口限流（集群安全、单 key 原子）。
 * <p>
 * 窗口为 {@code [now - window, now]}：先剔除过期时间戳成员，再判断当前成员数是否已达上限；
 * 未达上限则写入本次请求的时间戳并刷新 key 过期时间。
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisSlidingWindow {

    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    private static final String LUA = """
            redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', ARGV[1])
            local c = redis.call('ZCARD', KEYS[1])
            if c >= tonumber(ARGV[2]) then return 0 end
            redis.call('ZADD', KEYS[1], ARGV[3], ARGV[4])
            redis.call('EXPIRE', KEYS[1], ARGV[5])
            return 1
            """;

    /**
     * 尝试占用一次滑动窗口配额。
     *
     * @param key        Redis 键（建议包含业务前缀 + 客户端标识）
     * @param maxPermits 窗口内允许的最大请求次数
     * @param window     窗口长度
     * @return true 表示放行并已记录；false 表示已超限
     */
    public static boolean tryAcquire(String key, int maxPermits, Duration window) {
        long now = System.currentTimeMillis();
        double removeThrough = now - window.toMillis();
        String member = UUID.randomUUID().toString();
        long expireSec = Math.max(window.toSeconds() * 2L, 60L);
        RScript script = CLIENT.getScript();
        List<Object> keys = Collections.singletonList(key);
        Long ok = script.eval(
                RScript.Mode.READ_WRITE,
                LUA,
                RScript.ReturnType.INTEGER,
                keys,
                removeThrough,
                maxPermits,
                (double) now,
                member,
                expireSec);
        return ok != null && ok == 1L;
    }
}
