package com.gionee.cas.throttle.impl;

import com.gionee.cas.throttle.api.LoginThrottleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by Leon.Yu on 2017/4/9.
 */
@Component
public class RedisLoginThrottleInfo implements LoginThrottleInfo {

    private static final String PREFIX = "IS_THROTTLED.";

    @Autowired
    private RedisTemplate<String, Boolean> isThrottledRedisTemplate;


    @Override
    public boolean isThrottled(String key) {
        String constructKey = constructKey(key);
        if (isThrottledRedisTemplate.hasKey(constructKey)) {
            return isThrottledRedisTemplate.boundValueOps(constructKey).get();
        }

        return false;
    }

    @Override
    public void setThrottled(String key) {
        String constructKey = constructKey(key);
        isThrottledRedisTemplate.boundValueOps(constructKey).set(true);
    }

    @Override
    public void removeThrottled(String key) {
        String constructKey = constructKey(key);
        isThrottledRedisTemplate.delete(constructKey);
    }

    private String constructKey(String key) {
        return PREFIX + key;
    }
}
