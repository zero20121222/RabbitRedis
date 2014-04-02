package com.rabbit.test;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class QueueOneListener{
    JedisPool pool;
    Jedis jedis;

    public QueueOneListener() {
        //创建一个pool
        pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);

        //创建jedis对象（从资源中得到）
        jedis = pool.getResource();
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
    public void sendRedis(Object foo){
        jedis.lpush("sendRedis" , foo.toString());
    }
}