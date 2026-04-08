package org.dromara.common.redis.aspect;

import cn.hutool.core.util.StrUtil;
import org.dromara.common.redis.annotation.CacheRedisson;
import org.dromara.common.redis.annotation.CacheRedissonEvict;
import org.dromara.common.redis.annotation.CacheRedissonPut;
import org.dromara.common.redis.annotation.CacheRedissonUpdate;
import org.dromara.common.redis.utils.RedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Aspect
public class CacheRedissonAspect {

    private final CacheManager cacheManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public CacheRedissonAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Pointcut("@annotation(org.dromara.common.redis.annotation.CacheRedisson)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(ann)")
    public Object aroundCacheRedisson(ProceedingJoinPoint pjp, CacheRedisson ann) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        String cacheName = buildCacheName(ann);
        Object key = buildKey(ann.key(), method, args, pjp.getTarget());
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return pjp.proceed();
        }
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null) {
            return wrapper.get();
        }
        String lockKey = buildLockKey(cacheName, key);
        RLock lock = RedisUtils.getClient().getLock(lockKey);
        boolean locked = false;
        try {
            locked = tryLockWithRetry(lock, ann.lockWaitSeconds(), ann.lockLeaseSeconds());
            if (locked) {
                Cache.ValueWrapper w2 = cache.get(key);
                if (w2 != null) {
                    return w2.get();
                }
                Object value = pjp.proceed();
                cache.put(key, value);
                return value;
            } else {
                int retry = 20;
                while (retry-- > 0) {
                    Cache.ValueWrapper w3 = cache.get(key);
                    if (w3 != null) {
                        return w3.get();
                    }
                    Thread.sleep(50);
                }
                Object value = pjp.proceed();
                Cache.ValueWrapper existing = cache.putIfAbsent(key, value);
                return existing != null ? existing.get() : value;
            }
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private Object buildKey(String spel, Method method, Object[] args, Object target) {
        return buildKey(spel, method, args, target, null);
    }

    private Object buildKey(String spel, Method method, Object[] args, Object target, Object result) {
        if (StrUtil.isBlank(spel)) {
            Object key = SimpleKeyGenerator.generateKey(args);
            return key == null ? SimpleKey.EMPTY : key;
        }
        EvaluationContext context = new StandardEvaluationContext(target);
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length && i < args.length; i++) {
                ((StandardEvaluationContext) context).setVariable(paramNames[i], args[i]);
                ((StandardEvaluationContext) context).setVariable("p" + i, args[i]);
                ((StandardEvaluationContext) context).setVariable("a" + i, args[i]);
            }
        }
        ((StandardEvaluationContext) context).setVariable("args", args);
        ((StandardEvaluationContext) context).setVariable("result", result);
        ((StandardEvaluationContext) context).setVariable("ret", result);
        Expression expression = parser.parseExpression(spel);
        Object key = expression.getValue(context);
        return key == null ? SimpleKey.EMPTY : key;
    }

    @Pointcut("@annotation(org.dromara.common.redis.annotation.CacheRedissonPut)")
    public void putPointcut() {
    }

    @Around("putPointcut() && @annotation(ann)")
    public Object aroundCacheRedissonPut(ProceedingJoinPoint pjp, CacheRedissonPut ann) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        String cacheName = buildCacheName(ann.name(), ann.ttl(), ann.maxIdle(), ann.maxSize(), ann.local());
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return pjp.proceed();
        }
        Object result = pjp.proceed();
        Object key = buildKey(ann.key(), method, args, pjp.getTarget(), result);
        String lockKey = buildLockKey(cacheName, key);
        doWithLock(lockKey, ann.lockWaitSeconds(), ann.lockLeaseSeconds(), () -> {
            cache.put(key, result);
            return null;
        });
        return result;
    }

    @Pointcut("@annotation(org.dromara.common.redis.annotation.CacheRedissonUpdate)")
    public void updatePointcut() {
    }

    @Around("updatePointcut() && @annotation(ann)")
    public Object aroundCacheRedissonUpdate(ProceedingJoinPoint pjp, CacheRedissonUpdate ann) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        String cacheName = buildCacheName(ann.name(), ann.ttl(), ann.maxIdle(), ann.maxSize(), ann.local());
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return pjp.proceed();
        }
        Object result = pjp.proceed();
        Object key = buildKey(ann.key(), method, args, pjp.getTarget(), result);
        String lockKey = buildLockKey(cacheName, key);
        doWithLock(lockKey, ann.lockWaitSeconds(), ann.lockLeaseSeconds(), () -> {
            cache.put(key, result);
            return null;
        });
        return result;
    }

    @Pointcut("@annotation(org.dromara.common.redis.annotation.CacheRedissonEvict)")
    public void evictPointcut() {
    }

    @Around("evictPointcut() && @annotation(ann)")
    public Object aroundCacheRedissonEvict(ProceedingJoinPoint pjp, CacheRedissonEvict ann) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        String cacheName = buildCacheName(ann.name(), ann.ttl(), ann.maxIdle(), ann.maxSize(), ann.local());
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return pjp.proceed();
        }
        if (ann.beforeInvocation()) {
            evict(cache, ann, method, args, pjp.getTarget(), null, cacheName);
            return pjp.proceed();
        }
        Object result = pjp.proceed();
        evict(cache, ann, method, args, pjp.getTarget(), result, cacheName);
        return result;
    }

    private void evict(Cache cache, CacheRedissonEvict ann, Method method, Object[] args, Object target, Object result, String cacheName) throws Throwable {
        if (ann.allEntries()) {
            String lockKey = buildLockKey(cacheName, "__all__");
            doWithLock(lockKey, ann.lockWaitSeconds(), ann.lockLeaseSeconds(), () -> {
                cache.clear();
                return null;
            });
            return;
        }
        Object key = buildKey(ann.key(), method, args, target, result);
        String lockKey = buildLockKey(cacheName, key);
        doWithLock(lockKey, ann.lockWaitSeconds(), ann.lockLeaseSeconds(), () -> {
            cache.evict(key);
            return null;
        });
    }

    private String buildCacheName(CacheRedisson ann) {
        return buildCacheName(ann.name(), ann.ttl(), ann.maxIdle(), ann.maxSize(), ann.local());
    }

    private String buildCacheName(String name, String ttl, String maxIdle, int maxSize, boolean local) {
        String ttlValue = StrUtil.blankToDefault(ttl, "0");
        String idleValue = StrUtil.blankToDefault(maxIdle, "0");
        int localValue = local ? 1 : 0;
        return name + "#" + ttlValue + "#" + idleValue + "#" + maxSize + "#" + localValue;
    }

    private String buildLockKey(String cacheName, Object key) {
        return "lock:" + cacheName + ":" + String.valueOf(key);
    }

    private boolean tryLockWithRetry(RLock lock, long waitSeconds, long leaseSeconds) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            if (lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS)) {
                return true;
            }
            if (i < 2) {
                Thread.sleep(50L * (i + 1));
            }
        }
        return false;
    }

    private <T> T doWithLock(String lockKey, long waitSeconds, long leaseSeconds, Callable<T> action) throws Throwable {
        RLock lock = RedisUtils.getClient().getLock(lockKey);
        boolean locked = false;
        try {
            locked = tryLockWithRetry(lock, waitSeconds, leaseSeconds);
            if (!locked) {
                return null;
            }
            return action.call();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
