// src/main/kotlin/com/example/forumbackend/service/RedisService.kt
package com.example.forumbackend.service

import com.example.forumbackend.entity.Post
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun set(key: String, value: Any, expiration: Long = 0) {
        if (expiration > 0) {
            redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.SECONDS)
        } else {
            redisTemplate.opsForValue().set(key, value)
        }
    }

    fun get(key: String): Any? = redisTemplate.opsForValue().get(key)

    fun delete(key: String) = redisTemplate.delete(key)

    fun hasKey(key: String): Boolean = redisTemplate.hasKey(key) ?: false

    // Cache trending posts
    fun cacheTrendingPosts(posts: List<Post>) {
        set("trending_posts", posts, 3600) // Cache for 1 hour
    }

    // Get cached trending posts
    @Suppress("UNCHECKED_CAST")
    fun getCachedTrendingPosts(): List<Post>? = get("trending_posts") as? List<Post>

    // Cache post view count
    fun cachePostViews(postId: Long, views: Long) {
        set("post:views:$postId", views, 86400) // Cache for 24 hours
    }

    // Get cached post view count
    fun getCachedPostViews(postId: Long): Long? = get("post:views:$postId") as? Long

    // Increment post view count in cache
    fun incrementPostViews(postId: Long) {
        val key = "post:views:$postId"
        if (hasKey(key)) {
            redisTemplate.opsForValue().increment(key)
        } else {
            set(key, 1L, 86400)
        }
    }
}