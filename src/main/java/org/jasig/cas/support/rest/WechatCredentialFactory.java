package org.jasig.cas.support.rest;

import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.RememberMeUsernamePasswordCredential;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;

/**
 * Created by Leon.Yu on 2017/11/6.
 *
 * @Author Leon.Yu
 */
public class WechatCredentialFactory implements CredentialFactory {

    @Override
    public Credential fromRequestBody(@NotNull final MultiValueMap<String, String> requestBody) {
        final String username = requestBody.getFirst("username");
        final String password = requestBody.getFirst("password");
        final Boolean rememberMe = Boolean.valueOf(requestBody.getFirst("rememberMe"));
        final Boolean wechatLogin = Boolean.valueOf(requestBody.getFirst("wechatLogin"));
        if (username == null || password == null) {
            throw new BadRequestException("Invalid payload. 'username' and 'password' form fields are required.");
        }

        if (wechatLogin) {
            final WechatClientIdCredential credential = new WechatClientIdCredential();
            credential.setUsername(username);
            credential.setPassword(password);
            credential.setRememberMe(rememberMe);
            credential.setWechatLogin(wechatLogin);
            return credential;
        } else {
            final RememberMeUsernamePasswordCredential credential = new RememberMeUsernamePasswordCredential();
            credential.setUsername(username);
            credential.setPassword(password);
            credential.setRememberMe(rememberMe);
            return credential;
        }
    }
}
