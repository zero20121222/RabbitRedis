package com.rabbit.util;

import com.rabbit.ActionInterface.RedisAction;
import com.rabbit.ActionInterface.RedisNoResultAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

/**
 * 实现Jedis对象的封装，实现操作
 */
public class RedisTemplate {
    private static Logger logger = LoggerFactory.getLogger(RedisTemplate.class);

    //封装一个pool池用于jedis对象的管理
    private Pool<Jedis> jedisPool;

    public RedisTemplate(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 通过观察者模式实现事件的回调处理（使用范型）
     *
     * @param jedisAction   需要执行的方法接口
     * @param <T>           执行发放的返回类型
     * @return T
     * @throws JedisException
     * 返回执行好的对象信息
     */
    public <T> T execute(RedisAction<T> jedisAction){
        T result = null;
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            result = jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
        } finally {
            //释放jedis资源
            closeResource(jedis, broken);
            return result;
        }
    }

    /**
     * 通过观察者模式实现事件的回调处理
     */
    public void execute(RedisNoResultAction jedisAction){
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            jedisAction.actionNoResult(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * 将jedis资源恢复到pool中
     *
     * @param jedis             jedis资源
     * @param connectionBroken  连接状态（根据不同的状态，使用不同的资源释放方式）
     */
    protected void closeResource(Jedis jedis, boolean connectionBroken) {
        if (jedis != null) {
            if (connectionBroken) {
                //当失败的连接的资源的方式
                jedisPool.returnBrokenResource(jedis);
            } else {
                //当成功的连接的资源的方式
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 获取jedisPool对象.
     */
    public Pool<Jedis> getJedisPool() {
        return jedisPool;
    }
}
