package com.gionee.cas.throttle.api;

/**
 * Created by Leon.Yu on 2017/4/5.
 */
public interface LoginThrottleInfo {
    boolean isThrottled(String key);

    void setThrottled(String key);

    void removeThrottled(String key);
}
