package com.rabbit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

/**
 * 实现Jedis对象的封装，实现操作
 */
public class JedisTemplate {
    private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

    //封装一个pool池用于jedis对象的管理
    private Pool<Jedis> jedisPool;

    public JedisTemplate(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * Execute with a call back action with result.
     */
    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            return jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * Execute with a call back action without result.
     */
    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;
        try {
            jedis = jedisPool.getResource();
            jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            logger.error("Redis connection lost.", e);
            broken = true;
            throw e;
        } finally {
            closeResource(jedis, broken);
        }
    }

    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    protected void closeResource(Jedis jedis, boolean connectionBroken) {
        if (jedis != null) {
            if (connectionBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * Get the internal JedisPool.
     */
    public Pool<Jedis> getJedisPool() {
        return jedisPool;
    }

    /**
     * Callback interface for template method.
     */
    public interface JedisAction<T> {
        T action(Jedis jedis);
    }

    /**
     * Callback interface for template method without result.
     */
    public interface JedisActionNoResult {
        void action(Jedis jedis);
    }
}
