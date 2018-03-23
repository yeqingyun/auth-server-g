package com.gionee.cas.webflow;

import com.gionee.cas.throttle.util.LoginThrottleInfoHolder;
import com.gionee.cas.util.RequestUtils;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.util.StringUtils;

/**
 * Created by Leon.Yu on 2017/4/5.
 */
public class CaptchaEvaluator {

    public String determineShowCaptcha() {
        String uniqueId = RequestUtils.getUniqueId(WebUtils.getHttpServletRequest());
        if (StringUtils.hasText(uniqueId)) {
            boolean isThrottled = LoginThrottleInfoHolder.get().isThrottled(uniqueId);
            return String.valueOf(isThrottled);
        } else {
            return "false";
        }
    }

    public void clearThrottleInfoAfterLoginSuccess() {
        String uniqueId = RequestUtils.getUniqueId(WebUtils.getHttpServletRequest());
        LoginThrottleInfoHolder.get().removeThrottled(uniqueId);
    }
}
