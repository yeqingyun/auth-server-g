package org.jasig.cas.web.flow;

import com.gionee.cas.util.SendEmailUtil;
import com.gionee.cas.util.SendMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.authentication.handler.support.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 忘记密码
 *
 * @author zhangjun
 * @date 2017/10/25
 */
@Component
public class ForgetAction extends AbstractAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserHandler userHandler;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 重置密码校验值缓存redis后的前缀
     */
    public static final String REDIS_KEY_PRE = "forget_pwd_";

    /**
     * 默认响应操作结果的key
     */
    public static final String RESULT_MSG = "resultMsg";

    /**
     * 最后发送验证码的key，此处可后期考虑放到session范围
     */
    public static final String LAST_SMS_TIMESTAMP = "last_sms_timestamp";

    /**
     * 限制最低发送验证码时间间隔
     */
    public static final long SEND_SMS_LIMIT_MILES = 2 * 60 * 1000;

    /**
     * 验证码长度
     */
    public static final int SMS_CODE_SIZE = 6;

    public static final Pattern USERNAME_REG = Pattern.compile("\\d+");

    public static final Pattern EMAIL_REG = Pattern.compile(".*@.*");

    public static final Pattern PHONE_REG = Pattern.compile("\\d{11}");

    @Value("${mail.usrname}")
    private String mailFrom;

    @Value("${server.name}")
    private String serverName;

    @Value("${reset.password.expire.time}")
    private int emailExpireTime;

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        return null;
    }

    /**
     * 检查提交过来的用户名以及邮箱
     *
     * @param context
     * @return
     */
    public boolean checkUsernameAndEmail(RequestContext context) {
        boolean isValidate = false;
        String username = context.getRequestParameters().get("username"),
                email = context.getRequestParameters().get("email");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            context.getFlashScope().put(RESULT_MSG, "工号与邮箱不能为空");
        } else if (!USERNAME_REG.matcher(username).matches()) {
            context.getFlashScope().put(RESULT_MSG, "工号格式不正确");
        } else if (!EMAIL_REG.matcher(email).matches()) {
            context.getFlashScope().put(RESULT_MSG, "邮箱格式不正确");
        } else {
            isValidate = userHandler.existByEmail(username, email);
            if (isValidate) {
                context.getFlowScope().put("username", username);
                context.getFlowScope().put("email", email);
                isValidate = true;
            } else {
                context.getFlashScope().put(RESULT_MSG, "工号与邮箱不匹配");
            }
        }
        return isValidate;
    }

    /**
     * 校验账号与手机号匹配
     *
     * @param context
     * @return
     */
    public boolean checkUsernameAndPhone(RequestContext context) {
        boolean isValidate = false;
        String username = context.getRequestParameters().get("username"),
                phone = context.getRequestParameters().get("phone");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(phone)) {
            context.getFlashScope().put(RESULT_MSG, "工号与手机号不能为空");
        } else if (!USERNAME_REG.matcher(username).matches()) {
            context.getFlashScope().put(RESULT_MSG, "工号格式不正确");
        } else if (!PHONE_REG.matcher(phone).matches()) {
            context.getFlashScope().put(RESULT_MSG, "手机号格式不正确");
        } else {
            isValidate = userHandler.existByPhone(username, phone);
            if (isValidate) {
                context.getFlowScope().put("username", username);
                context.getFlowScope().put("phone", phone);
                isValidate = true;
            } else {
                context.getFlashScope().put(RESULT_MSG, "工号与手机号不匹配");
            }
        }
        return isValidate;
    }

    /**
     * 向目标邮箱发送包含校验码的邮件，并将校验值缓存至redis
     *
     * @param context
     * @return
     */
    public Event sendConfirmMail(RequestContext context) {
        String result = "fail";
        String username = context.getFlowScope().get("username").toString(), email = context.getFlowScope().get("email").toString();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            context.getFlowScope().put(RESULT_MSG, "工号与邮箱不能为空");
        } else {
            try {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");

                String url = serverName + (serverName.endsWith("/") ? "" : "/") + "forget?u=" + username + "&v=" + uuid;
                if (context.getFlowScope().contains("url")) {
                    url += "&url=" + context.getFlowScope().get("url");
                }
                //设置redis值
                redisTemplate.opsForValue().set(REDIS_KEY_PRE + username, uuid, emailExpireTime, TimeUnit.MINUTES);
                //发送邮件
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "GBK");
                helper.setFrom(mailFrom);
                helper.setText(SendEmailUtil.createForgetPasswordEmail(username, url, emailExpireTime), true);
                helper.setSubject("统一认证平台 密码找回");
                helper.setTo(email);
                //此处改异步发送邮件，避免前端卡顿
                new Thread(new SendEmailUtil(javaMailSender, message)).start();
                result = "success";
                context.getFlashScope().put(RESULT_MSG, "重置密码链接已发送至邮箱");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
                context.getFlashScope().put(RESULT_MSG, e.getMessage());
            }
        }

        return new Event(this, result);
    }

    /**
     * 发送手机验证码
     *
     * @param context
     */
    public void sendConfirmSMS(RequestContext context) {
        String username = (String) context.getFlowScope().get("username"), phone = (String) context.getFlowScope().get("phone");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(phone)) {
            context.getFlashScope().put(RESULT_MSG, "参数错误");
            return;
        }
        Long lastSendSmsTimestamp = (Long) context.getFlowScope().get(LAST_SMS_TIMESTAMP);
        if (lastSendSmsTimestamp != null && System.currentTimeMillis() - lastSendSmsTimestamp < SEND_SMS_LIMIT_MILES) {
            return;
        }
        String code = SendMessageUtil.createCode(SMS_CODE_SIZE);
        redisTemplate.opsForValue().set(REDIS_KEY_PRE + username, code, emailExpireTime, TimeUnit.MINUTES);
        context.getFlowScope().put(LAST_SMS_TIMESTAMP, System.currentTimeMillis());
        new Thread(new SendMessageUtil("你的统一认证平台工号[ " + username + " ]正在重置密码，验证码是[ " + code + " ]，有效期" + emailExpireTime + "分钟，如非你本人操作，请忽略此短信", phone)).start();
    }

    /**
     * 校验修改密码链接所含值是否均匹配，匹配则将目标用户名及邮箱放到flowScope内
     *
     * @param context
     * @return
     */
    public boolean validateConfirmURL(RequestContext context) {
        boolean isValidate = false;
        String username = context.getRequestParameters().get("u"), validate = context.getRequestParameters().get("v");
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(validate)) {
            String uuid = redisTemplate.opsForValue().get(REDIS_KEY_PRE + username);
            if (StringUtils.isNotEmpty(uuid) && validate.equals(uuid)) {
                context.getFlowScope().put("username", username);
                isValidate = true;
            } else {
                context.getFlashScope().put(RESULT_MSG, "链接已失效");
            }
        }
        return isValidate;
    }

    /**
     * 校验短信验证码
     *
     * @param context
     * @return
     */
    public boolean validateSMS(RequestContext context) {
        boolean isValidate = false;
        String username = (String) context.getFlowScope().get("username"), phone = (String) context.getFlowScope().get("phone"), code = context.getRequestParameters().get("code");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(phone)) {
            context.getFlashScope().put(RESULT_MSG, "参数错误");
        } else if (StringUtils.isEmpty(code)) {
            context.getFlashScope().put(RESULT_MSG, "验证码不能为空");
        } else {
            String realCode = redisTemplate.opsForValue().get(REDIS_KEY_PRE + username);
            if (StringUtils.isEmpty(realCode)) {
                context.getFlashScope().put(RESULT_MSG, "失效验证码");
            } else if (!realCode.equals(code)) {
                context.getFlashScope().put(RESULT_MSG, "验证码不正确");
            } else {
                isValidate = true;
            }
        }
        return isValidate;

    }

    /**
     * 确定修改密码
     *
     * @param context
     * @return
     */
    public Event changePassword(RequestContext context) {
        String result = "fail";
        String username = context.getFlowScope().get("username").toString(), pwd = context.getRequestParameters().get("password"), re_pwd = context.getRequestParameters().get("repeat_password");

        if (StringUtils.isEmpty(username)) {
            context.getFlashScope().put(RESULT_MSG, "参数不正确");
        } else if (StringUtils.isEmpty(pwd) || StringUtils.isEmpty(re_pwd)) {
            context.getFlashScope().put(RESULT_MSG, "密码与重复密码不能为空");
        } else if (!pwd.equals(re_pwd)) {
            context.getFlashScope().put(RESULT_MSG, "密码与重复密码不一致");
        } else {
            try {
                if (userHandler.resetPwd(username, pwd)) {
                    redisTemplate.delete(REDIS_KEY_PRE + username);
                    context.getFlashScope().put(RESULT_MSG, "操作成功");
                    result = "success";
                } else {
                    context.getFlashScope().put(RESULT_MSG, "重置失败，请重试");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
                context.getFlashScope().put(RESULT_MSG, e.getMessage());
            }
        }
        return new Event(this, result);

    }

    /**
     * 将来源url存到flow范围内
     *
     * @param context
     */
    public void setURL(RequestContext context) {
        if (!context.getFlowScope().contains("url") && context.getRequestParameters().contains("url")) {
            context.getFlowScope().put("url", context.getRequestParameters().get("url"));
        }
    }

}
