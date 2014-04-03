package com.rabbit.test;

import com.rabbit.ActionInterface.RedisNoResultAction;
import com.rabbit.util.RedisKeys;
import com.rabbit.util.RedisLock;
import com.rabbit.util.RedisLockModel;
import com.rabbit.util.RedisTemplate;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

@Component
public class QueueOneListener{
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(QueueOneListener.class);

    @Before
    public void setUp(){
        //销毁所有已经创建的锁
        RedisLock.destructionLocks(redisTemplate);
    }

    public void listenOne(Object foo) {
        System.out.println("listen1");
        System.out.println(foo);
    }

    public void listenTwo(Object foo) {
        System.out.println("listen2");
        System.out.println(foo);
    }

    public void listenThree(Object foo) {
        System.out.println("listen3");
        System.out.println(foo);
    }

    public void listenThree1(Object foo){
        System.out.println("listenThree->"+foo);
    }

    /**
     * 实现将信息塞入到redis中
     * @param foo   对象信息
     */
    public void sendRedis(final Object foo){
        //这个是针对分布式环境下的锁机制
        RedisLockModel redisLockModel = RedisLock.acquireLock(redisTemplate , RedisKeys.REDIS_TEST, 1000, (int)TimeUnit.SECONDS.toSeconds(10));

        try{
            if(RedisLock.ACQUIRE_RESULT(redisLockModel)){
                redisTemplate.execute(new RedisNoResultAction() {
                    @Override
                    public void actionNoResult(Jedis jedis) {
                        jedis.lpush("sendRedis:" , foo.toString());
                    }
                });
            }else{
                log.debug("acquire lock is failed!");
            }
        } catch (Exception e){
            log.error("send Redis failed , error={}", e);
        }finally {
            //释放锁
            //RedisLock.releaseLock(redisTemplate, redisLockModel);
        }
    }
}