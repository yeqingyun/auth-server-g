package com.gionee.cas.throttle.impl;

import com.gionee.cas.throttle.api.LoginThrottleInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Leon.Yu on 2017/4/5.
 */
@Component
public class InMemoryLoginThrottleInfo implements LoginThrottleInfo {

    private Map<String, Boolean> loginThrottledCache = new ConcurrentHashMap<>();


    @Override
    public boolean isThrottled(String key) {
        Boolean isThrottled = false;
        if (loginThrottledCache.containsKey(key)) {
            isThrottled = loginThrottledCache.get(key);
        }

        return isThrottled;
    }

    @Override
    public void setThrottled(String key) {
        loginThrottledCache.put(key, true);
    }

    @Override
    public void removeThrottled(String key) {
        loginThrottledCache.remove(key);
    }
}
