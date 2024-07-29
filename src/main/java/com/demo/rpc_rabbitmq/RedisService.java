package com.demo.rpc_rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Boolean> redisTemplate;

    public void save(String correlationId) {
        redisTemplate.opsForValue().set(correlationId, true, 5, TimeUnit.MINUTES);
    }

    public boolean isValidCorrelationId(String correlationId) {
        Boolean hasCorrelationId = redisTemplate.opsForValue().get(correlationId);
        if (hasCorrelationId != null) {
            redisTemplate.delete(correlationId);
            return hasCorrelationId;
        }
        return false;
    }
}
