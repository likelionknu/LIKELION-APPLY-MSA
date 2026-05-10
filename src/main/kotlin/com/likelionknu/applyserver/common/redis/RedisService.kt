package com.likelionknu.applyserver.common.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val values = redisTemplate.opsForValue()

    fun setValues(key: String, data: String) {
        values.set(key, data)
    }

    fun setValues(key: String, data: String, duration: Duration) {
        values.set(key, data, duration)
    }

    fun getValues(key: String): Any? = values.get(key)

    fun deleteValues(key: String) {
        redisTemplate.delete(key)
    }
}