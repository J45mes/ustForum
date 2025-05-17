package com.example.forumbackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration

@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(
        @Value("\${spring.redis.host}") host: String,
        @Value("\${spring.redis.port}") port: Int
    ): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port)
        
        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(2))  // Set command timeout to 2 seconds
            .shutdownTimeout(Duration.ofMillis(100))
            .build()
            
        return LettuceConnectionFactory(config, clientConfig)
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        
        // Use StringRedisSerializer for keys
        template.keySerializer = StringRedisSerializer()
        
        // Use Jackson serializer for values
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        
        // Also set serializers for hash operations
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()
        
        template.afterPropertiesSet()
        return template
    }
} 