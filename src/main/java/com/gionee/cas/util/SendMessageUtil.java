package com.gionee.cas.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 通过调用短信平台短信接口发送短信
 *
 * @author
 * @date
 */
public class SendMessageUtil implements Runnable {

    private static final String MSGURL = "http://msg.gionee.com/send_message/send1.json";
    private static final String MSGACCOUNT = "otherSys";
    private static final Logger logger = LoggerFactory.getLogger(SendMessageUtil.class);

    private String content;
    private String mobile;

    private SendMessageUtil() {
    }

    public SendMessageUtil(String content, String mobile) {
        Assert.notNull(content, "the content should not be null");
        Assert.notNull(mobile, "the mobile number should not be null");
        this.content = content;
        this.mobile = mobile;
    }

    public static String createCode(int size) {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < size; i++) {
            code += random.nextInt(9);
        }
        return code;
    }


    //参数说明:content:短信内容,mobile:手机号码
    @Override
    public void run() {
        HttpClientBuilder httpBuilder = HttpClientBuilder.create();
        // 创建默认的httpClient实例
        CloseableHttpClient httpClient = httpBuilder.build();
        RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(20000);

        //发送短信接口,短信平台短信接口
        HttpPost httpPost = new HttpPost(MSGURL);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");

        Map<String, String> reqData = new HashMap<String, String>();
        //具备发送短信权限的账号
        reqData.put("account", MSGACCOUNT);
        reqData.put("message_content", content);
        reqData.put("destination_number", mobile);

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            for (Map.Entry<String, String> entry : reqData.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            //设置编码
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpClient.execute(httpPost);

            logger.info("------message content:------" + content);
        } catch (ConnectTimeoutException cte) {
            cte.printStackTrace();
            logger.info("message send fail");
        } catch (SocketTimeoutException ste) {
            ste.printStackTrace();
            logger.info("message send fail");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("message send fail");
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
