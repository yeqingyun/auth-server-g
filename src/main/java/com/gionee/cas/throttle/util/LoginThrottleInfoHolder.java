package com.gionee.cas.throttle.util;

import com.gionee.cas.throttle.api.LoginThrottleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by Leon.Yu on 2017/4/5.
 */
@Component
public class LoginThrottleInfoHolder {
    private static LoginThrottleInfo loginThrottleInfo = null;

    private LoginThrottleInfoHolder() {

    }

    public static LoginThrottleInfo get() {
        return loginThrottleInfo;
    }

    @Autowired
    @Qualifier("defaultLoginThrottleInfo")
    public void setLoginThrottleInfo(final LoginThrottleInfo loginThrottleInfo) {
        LoginThrottleInfoHolder.loginThrottleInfo = loginThrottleInfo;
    }
}
