package org.dromara.common.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRedissonUpdate {
    String name();
    String key() default "";
    String ttl() default "0";
    String maxIdle() default "0";
    int maxSize() default 0;
    boolean local() default true;
    long lockWaitSeconds() default 5;
    long lockLeaseSeconds() default 20;
}
