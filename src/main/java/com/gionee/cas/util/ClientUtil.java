package com.gionee.cas.util;

/**
 * Created by doit on 2016/1/19.
 */
public class ClientUtil {

    public static boolean isSimpleLogin(String userAgent) {
        return userAgent != null && userAgent.contains("simpleLogin");
    }

    public static boolean isMobileClient(String header) {
        String[] deviceArray = new String[]{"android", "mac os", "windows phone"};
        if (header == null) {
            return false;
        }
        header = header.toLowerCase();
        if (header.matches("^android[0-9]+.*$")) {
            return false;
        }
        for (String device : deviceArray) {
            if (header.indexOf(device) > 0) {
                return true;
            }
        }
        return false;
    }

}
