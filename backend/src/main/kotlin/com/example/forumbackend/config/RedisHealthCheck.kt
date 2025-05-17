package com.example.forumbackend.config

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisHealthCheck(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisConnectionFactory: RedisConnectionFactory
) : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(RedisHealthCheck::class.java)
    
    override fun run(vararg args: String?) {
        try {
            val connection = redisConnectionFactory.connection
            connection.ping()
            connection.close()
            logger.info("✅ Successfully connected to Redis")
            
            // Test a simple operation
            redisTemplate.opsForValue().set("test:health", "OK")
            val value = redisTemplate.opsForValue().get("test:health")
            logger.info("✅ Redis operation test: $value")
            redisTemplate.delete("test:health")
        } catch (e: Exception) {
            logger.error("❌ Failed to connect to Redis: ${e.message}", e)
        }
    }
} 