package org.jasig.cas.authentication.handler.support;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 针对用户重置密码的接口操作日志
 *
 * @author zhangjun
 * @date 2017/11/1
 */
@Aspect
@Component
public class UserResetPasswordLogAdvice {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public static final String SQL = "insert into com_audit_trail(aud_action,aud_user,aud_resource,aud_date) values(?,?,?,?)";

    @AfterReturning(value = "execution(* org.jasig.cas.authentication.handler.support.UserHandler.resetPwd(..))", returning = "result")
    public void logResetSuccess(JoinPoint joinPoint, boolean result) {
        if (result) {
            Object[] args = joinPoint.getArgs();
            jdbcTemplate.update(SQL, "RESET_PASSWORD", args[0], "用户成功重置密码", new Date());
        }
    }

    @AfterReturning(value = "execution(* org.jasig.cas.authentication.handler.support.UserHandler.existByEmail(..))", returning = "result")
    public void logResetApplyByEmail(JoinPoint joinPoint, boolean result) {
        if (result) {
            Object[] args = joinPoint.getArgs();
            jdbcTemplate.update(SQL, "APPLY_RESET_PASSWORD", args[0], "用户请求重置密码，邮箱[" + args[1] + "]", new Date());
        }
    }

    @AfterReturning(value = "execution(* org.jasig.cas.authentication.handler.support.UserHandler.existByPhone(..))", returning = "result")
    public void logResetApplyByPhone(JoinPoint joinPoint, boolean result) {
        if (result) {
            Object[] args = joinPoint.getArgs();
            jdbcTemplate.update(SQL, "APPLY_RESET_PASSWORD", args[0], "用户请求重置密码，手机号[" + args[1] + "]", new Date());
        }
    }


}
