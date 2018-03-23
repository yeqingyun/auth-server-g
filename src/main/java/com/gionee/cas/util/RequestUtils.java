package com.gionee.cas.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Leon.Yu on 2017/4/15.
 */
public class RequestUtils {

    public static String getUniqueId(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
