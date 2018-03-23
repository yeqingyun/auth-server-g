package com.gionee.cas.util;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

/**
 * @author zhangjun
 * @date 2017/11/1
 */
public class SendEmailUtil implements Runnable {


    private JavaMailSender javaMailSender;
    private MimeMessage message;

    private SendEmailUtil() {
    }

    public SendEmailUtil(JavaMailSender javaMailSender, MimeMessage message) {
        this.javaMailSender = javaMailSender;
        this.message = message;
    }

    public static String createForgetPasswordEmail(String username, String url, int emailExpireTime) {
        StringBuilder sb = new StringBuilder("你的统一认证平台工号[ " + username + " ]正在重置密码，确认操作请点击链接：");
        sb.append("<br/><br/>");
        sb.append("<a href=\"" + url + "\">" + url + "</a>");
        sb.append("<br/><br/>");
        sb.append("如果你的email程序不支持链接点击，请将上面的地址拷贝至你的浏览器(例如IE)的地址栏进入");
        sb.append("<br/><br/>");
        sb.append("此链接" + emailExpireTime + "分钟内有效，如非你本人操作，请忽略此邮件");
        sb.append("<br/><br/>");
        sb.append("(这是一封自动产生的email，请勿回复)");
        return sb.toString();
    }

    @Override
    public void run() {
        this.javaMailSender.send(this.message);
    }
}
