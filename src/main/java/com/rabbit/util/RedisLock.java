package com.rabbit.util;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.rabbit.ActionInterface.RedisAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 通过redis实现对于分布式的环境下的并发管理（通过redis锁实现并发处理）
 */
public class RedisLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);

    /**
     * 创建一个redis锁
     * @param redisTemplate     redis处理对象
     * @param redisKeys         需要添加的锁对象
     * @param maxWait           最长的添加锁时的等待时间(单位:ms)
     * @param expiredTime       设置锁的过期时间(单位:s)
     * @return  String
     * 返回锁的唯一uuid
     */
    public static RedisLockModel acquireLock(final RedisTemplate redisTemplate, final RedisKeys redisKeys, final Integer maxWait, final Integer expiredTime) {
        //随即获取一个uuid，作为一个key的标识区别与，同名lock
        final String uuid = UUID.randomUUID().toString();

        Boolean result = redisTemplate.execute(new RedisAction<Boolean>(){
            @Override
            public Boolean action(Jedis jedis) {
                //获取等待时间的最后时间段
                long expireAt = System.currentTimeMillis()+maxWait;
                //循环判断锁是否一直存在
                while (System.currentTimeMillis() < expireAt){
                    LOGGER.debug("try lock key={} ", redisKeys.toString());
                    if (jedis.setnx(redisKeys.toString(), uuid) == 1) {
                        jedis.expire(redisKeys.toString(), expiredTime);
                        LOGGER.debug("get lock, key={}, expire in seconds={}", redisKeys.toString(), expiredTime);
                        return true;
                    } else {
                        // 存在锁（会一直执行（在最大等待时间范围内）等待该锁释放）
                        String desc = jedis.get(redisKeys.toString());
                        LOGGER.debug("key={} locked by another business={}", redisKeys.toString(), desc);
                    }

                    try{
                        TimeUnit.MILLISECONDS.sleep(1);
                    }catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                //在最大等待时间范围内未获取到锁
                return false;
            }
        });

        RedisLockModel redisLockModel = new RedisLockModel(redisKeys.toString());
        redisLockModel.setLockId(result ? uuid : null);
        return redisLockModel;
    }

    /**
     * 通过获得的锁编号判断锁添加是否成功
     * @param redisLockModel    redis锁对象
     * @return  Boolean
     * 返回是否添加成功
     */
    public static Boolean ACQUIRE_RESULT(RedisLockModel redisLockModel){
        return !Strings.isNullOrEmpty(redisLockModel.getLockId());
    }

    //释放锁
    public static Boolean releaseLock(final RedisTemplate redisTemplate , final RedisLockModel redisLockModel) {
        return redisTemplate.execute(new RedisAction<Boolean>() {
            @Override
            public Boolean action(Jedis jedis) {
                //防止多个服务器处理同一个key需要watch操作(相当于是禁止了其他client处理这个key)
                jedis.watch(redisLockModel.getLockName());

                //如果锁没有过期,则锁的值必然没有改变
                if (Objects.equal(redisLockModel.getLockId(), jedis.get(redisLockModel.getLockName()))) {
                    //删除锁
                    jedis.del(redisLockModel.getLockName());
                    return true;
                }

                //锁已经过期了，释放watch操作
                jedis.unwatch();
                return false;
            }
        });
    }

    //批量释放锁
    public static Integer releaseBatchLock(final RedisTemplate redisTemplate , final List<RedisLockModel> redisLockModels){
        return redisTemplate.execute(new RedisAction<Integer>() {
            @Override
            public Integer action(Jedis jedis) {
                Transaction transaction = jedis.multi();

                LOGGER.debug("release batch redis lock start");
                for(RedisLockModel redisLockModel : redisLockModels){
                    if(Objects.equal(redisLockModel.getLockId() , jedis.get(redisLockModel.getLockName()))){
                        //删除未过期的锁
                        transaction.del(redisLockModel.getLockName());
                    }
                }

                //事务提交操作(返回执行了多少条事务)
                Integer length = transaction.exec().size();
                LOGGER.debug("release batch redis lock end, release num={}", length);

                return length;
            }
        });
    }

    /**
     * 销毁全部所有创建的lock
     *
     * @param redisTemplate     redis操作对象
     * @return  Integer
     * 返回总共销毁的创建条数
     */
    public static Integer destructionLocks(final RedisTemplate redisTemplate){
        return redisTemplate.execute(new RedisAction<Integer>() {
            @Override
            public Integer action(Jedis jedis) {
                Transaction transaction = jedis.multi();

                LOGGER.debug("destruction redis lock start");
                for(RedisKeys redisKeys : RedisKeys.values()){
                    transaction.del(redisKeys.toString());
                }

                //事务提交操作(返回执行了多少条事务)
                Integer length = transaction.exec().size();
                LOGGER.debug("destruction redis lock end, release num={}", length);

                return length;
            }
        });
    }
}