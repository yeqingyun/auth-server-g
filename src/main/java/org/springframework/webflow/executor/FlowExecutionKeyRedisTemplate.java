package org.springframework.webflow.executor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.webflow.execution.FlowExecutionKey;

/**
 * Created by Leon.Yu on 2017/4/14.
 */
public class FlowExecutionKeyRedisTemplate extends RedisTemplate<String, FlowExecutionKey> {

    private static final String PREFIX = "COMPAT_APP.";

    public String getKeyByUniqueId(String uniqueId) {
        return PREFIX + uniqueId;
    }

    public FlowExecutionKeyRedisTemplate() {
        final RedisSerializer<String> string = new StringRedisSerializer();
        final JdkSerializationRedisSerializer jdk = new JdkSerializationRedisSerializer();
        setKeySerializer(string);
        setValueSerializer(jdk);
        setHashKeySerializer(string);
        setHashValueSerializer(jdk);
    }
}
