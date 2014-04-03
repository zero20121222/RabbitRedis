package com.rabbit.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zero on 14-4-3.
 */

public class RedisLockModel{
    //锁id
    @Setter
    @Getter
    private String lockId;

    //锁name
    @Getter
    private String lockName;

    public RedisLockModel(String lockName){
        this.lockName = lockName;
    }
}
