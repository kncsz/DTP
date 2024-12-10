package org.swu.util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) throws JsonProcessingException {
        String jsonValue = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, jsonValue, timeout, unit);
    }

    public <T> T get(String key, Class<T> clazz) throws JsonProcessingException {
        String jsonValue = redisTemplate.opsForValue().get(key);
        return jsonValue != null ? objectMapper.readValue(jsonValue, clazz) : null;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
