package com.gionee.cas.throttle.redistemplate;

import com.gionee.cas.throttle.api.LoginRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * Created by Leon.Yu on 2017/4/11.
 */
@Component
public class LoginRecordRedisTemplate extends RedisTemplate<String, LoginRecord> {

    public LoginRecordRedisTemplate() {
        final RedisSerializer<String> string = new StringRedisSerializer();
        final JdkSerializationRedisSerializer jdk = new JdkSerializationRedisSerializer();
        setKeySerializer(string);
        setValueSerializer(jdk);
        setHashKeySerializer(string);
        setHashValueSerializer(jdk);
    }

    @Autowired
    @Override
    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        super.setConnectionFactory(connectionFactory);
    }
}
