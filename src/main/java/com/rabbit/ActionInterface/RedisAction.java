package com.rabbit.ActionInterface;

import redis.clients.jedis.Jedis;

/**
 * 通过范型定义redis的操作的返回对象
 * @param <T>   redis操作的返回对象
 */
public interface RedisAction<T> {
    /**
     * 实现观察者模式的对象观察者方法
     * @param jedis     资源对象
     * @return  T
     * 返回调用结果
     */
    public T action(Jedis jedis);
}
