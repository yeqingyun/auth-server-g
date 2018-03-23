package com.gionee.cas.adaptors.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.support.rest.WechatClientIdCredential;

import java.security.GeneralSecurityException;

/**
 * 兼容微信使用ClientId方式登录的JDBC验证处理类
 * Created by Leon.Yu on 2017/7/6.
 */
public class QueryDatabaseAuthenticationCompatWechatLoginHandler extends QueryDatabaseAuthenticationHandler {

    @Override
    protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)
            throws GeneralSecurityException, PreventedException {
        if (credential instanceof WechatClientIdCredential && ((WechatClientIdCredential) credential).isWechatLogin()) {
            if (StringUtils.isBlank(this.sql) || getJdbcTemplate() == null) {
                throw new GeneralSecurityException("Authentication handler is not configured correctly");
            }

            final String username = credential.getUsername();
            final String encryptedPassword = credential.getPassword();
            validateUsernameAndPassword(username, encryptedPassword);
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username), null);
        } else {
            return super.authenticateUsernamePasswordInternal(credential);
        }
    }
}
