package org.jasig.cas.authentication.handler.support;

/**
 * 用户找回密码功能设置的接口类
 *
 * @author zhangjun
 * @date 2017/10/28
 */
public interface UserHandler {

    /**
     * 验证传入的帐户名（工号），邮箱是否匹配
     *
     * @param account
     * @param email
     * @return
     */
    boolean existByEmail(String account, String email);

    /**
     * 验证传入的帐户名，手机号是否匹配
     * @param account
     * @param phone
     * @return
     */
    boolean existByPhone(String account, String phone);

    /**
     * 重设密码
     *
     * @param account
     * @param pwd     原始密码，未加密
     * @return
     */
    boolean resetPwd(String account, String pwd);
}
