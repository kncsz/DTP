package org.swu.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 设置键（key）的序列化采用StringRedisSerializer。
        template.setKeySerializer(new StringRedisSerializer());

        // 设置值（value）的序列化采用GenericToStringSerializer。
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));

        // 设置哈希键（hash key）的序列化采用StringRedisSerializer。
        template.setHashKeySerializer(new StringRedisSerializer());

        // 设置哈希值（hash value）的序列化采用GenericToStringSerializer。
        template.setHashValueSerializer(new GenericToStringSerializer<Object>(Object.class));

        // 初始化RedisTemplate。
        template.afterPropertiesSet();
        return template;
    }
}
