package com.gionee.cas.util;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Leon.Yu on 2017/4/15.
 */
public class EoaConfiguration {

    private static String eoaUrl;

    public static String getEoaUrl() {
        return EoaConfiguration.eoaUrl;
    }

    @Value("${eoa.url:}")
    public void setEoaUrl(String eoaUrl) {
        EoaConfiguration.eoaUrl = eoaUrl;
    }
}
