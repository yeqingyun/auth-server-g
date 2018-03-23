package org.jasig.cas.adaptors.jdbc;

import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.UserHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * 基于jdbc的userHandler实现
 *
 * @author zhangjun
 * @date 2017/10/28.
 */
public class QueryUserHandler implements UserHandler {

    private JdbcTemplate jdbcTemplate;

    private JdbcTemplate jdbcTemplateHr;

    private PasswordEncoder passwordEncoder;

    /**
     * 私有构造器，以上几个属性必须构造传入，因考虑后期改用ldap，此处多个jdbc连接可能会弃用，于是未使用spring自动注入
     */
    private QueryUserHandler() {
    }

    public QueryUserHandler(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplateHr, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcTemplateHr = jdbcTemplateHr;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean existByEmail(String account, String email) {
        Assert.notNull(account, "the account should not be null");
        Assert.notNull(email, "the email should not be null");

        Integer nums = (Integer) jdbcTemplateHr.queryForMap("select count(1) as nums from sapadressbook where type = 'MAIL' and empid = ? and bookno = ? ", account, email).get("nums");

        return nums > 0;
    }

    @Override
    public boolean resetPwd(String account, String pwd) {
        Assert.notNull(account, "the account should not be null");
        Assert.notNull(pwd, "the password ready to be set should not be null");
        int result = jdbcTemplate.update("update usr set pwd = ? where empid = ?", passwordEncoder.encode(pwd), account);
        return result > 0;
    }

    @Override
    public boolean existByPhone(String account, String phone) {
        Assert.notNull(account, "the account should not be null");
        Assert.notNull(phone, "the email should not be null");
        Integer nums = (Integer) jdbcTemplateHr.queryForMap("select count(1) as nums from sapadressbook where type = 'CELL' and empid = ? and bookno = ? ", account, phone).get("nums");
        return nums > 0;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public JdbcTemplate getJdbcTemplateHr() {
        return jdbcTemplateHr;
    }

    public void setJdbcTemplateHr(JdbcTemplate jdbcTemplateHr) {
        this.jdbcTemplateHr = jdbcTemplateHr;
    }
}
