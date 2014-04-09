package com.rabbit.annotation;

import com.rabbit.util.RedisLockExecute;
import com.rabbit.util.RedisLockModel;
import com.rabbit.util.RedisTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通过redis实现对于分布式的环境下的并发管理（通过redis锁实现并发处理）
 * 通过注解实现
 */
@Aspect
@Component
public class RedisLockOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockOperation.class);

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 创建一个redis锁
     * @param proceed     实际处理对象
     * @param redisLock   RedisLock注解对象
     */
    @Around("execution (* *(..)) && @annotation(redisLock)")
    public void acquireLock(final ProceedingJoinPoint proceed, final RedisLock redisLock) {
        RedisLockModel redisLockModel = RedisLockExecute.acquireLock(redisTemplate , redisLock.redisKeys(), redisLock.maxWait(), redisLock.expiredTime());

        try{
            if(RedisLockExecute.ACQUIRE_RESULT(redisLockModel)){
                proceed.proceed();
            }else{
                LOGGER.debug("acquire lock is failed!");
            }
        } catch (Throwable throwable) {
            LOGGER.error("proceed={} is failed!", proceed.toString());
            throwable.printStackTrace();
        }finally {
            RedisLockExecute.releaseLock(redisTemplate , redisLockModel);
        }
    }
}