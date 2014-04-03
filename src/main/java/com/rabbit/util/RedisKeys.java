package com.rabbit.util;

/**
 * Redis操作时候的key的枚举类型
 * Created by zero on 14-4-3.
 */
public enum RedisKeys {
    REDIS_TEST(1 , "REDIS_TEST") , RABBIT_TEST(2 , "RABBIT_TEST");

    private Integer index;
    private String keyName;

    private RedisKeys(Integer index , String keyName){
        this.index = index;
        this.keyName = keyName;
    }

    public static RedisKeys fromIndex(int index) {
        for (RedisKeys redisKeys: RedisKeys.values()) {
            if (redisKeys.index == index) {
                return redisKeys;
            }
        }
        return null;
    }

    public Integer value() {
        return index;
    }

    @Override
    public String toString() {
        return keyName;
    }
}
