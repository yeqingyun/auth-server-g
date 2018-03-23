package com.gionee.cas.support;

import org.jasig.cas.authentication.RememberMeUsernamePasswordCredential;

/**
 * Created by Leon.Yu on 2017/7/6.
 */
public class WechatClientIdCredential extends RememberMeUsernamePasswordCredential {
    private boolean wechatLogin;

    public boolean isWechatLogin() {
        return wechatLogin;
    }

    public void setWechatLogin(boolean wechatLogin) {
        this.wechatLogin = wechatLogin;
    }
}
