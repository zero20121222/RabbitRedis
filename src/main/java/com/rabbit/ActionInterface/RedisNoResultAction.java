package com.rabbit.ActionInterface;

import redis.clients.jedis.Jedis;

/**
 * 通过范型定义redis的操作的返回对象
 */
public interface RedisNoResultAction{
    /**
     * 实现观察者模式的对象观察者方法(不产生返回结果)
     * @param jedis     资源对象
     */
    public void actionNoResult(Jedis jedis);
}
