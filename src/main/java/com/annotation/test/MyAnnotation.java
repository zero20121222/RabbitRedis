package com.annotation.test;

import java.lang.annotation.*;

/**
 * 这个是一个annotation的测试类
 * Created by zero on 14-4-6.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
    //无用的一个参数
    int value() default 0;

    //redis锁的键名称
    String lockName() default "";

    //最长等待时间
    int maxWait() default 0;

    //redis同步锁的过期时间
    int expiredTime() default 0;
}
