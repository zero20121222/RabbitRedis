package com.rabbit.annotation;

import com.rabbit.util.RedisKeys;

import java.lang.annotation.*;

/**
 * 使用注解方式实现redis分布式锁操作的设计
 * Created by zero on 14-4-8.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    //redis的key对象
    RedisKeys redisKeys();

    //最长等待时间
    int maxWait();

    //键的过期时间
    int expiredTime();
}
