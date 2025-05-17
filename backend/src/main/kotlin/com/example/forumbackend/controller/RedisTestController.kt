package com.example.forumbackend.controller

import com.example.forumbackend.service.RedisService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/redis-test")
class RedisTestController(private val redisService: RedisService) {

    @PostMapping("/set")
    fun setKey(@RequestParam key: String, @RequestParam value: String): ResponseEntity<String> {
        redisService.set(key, value)
        return ResponseEntity.ok("Value set successfully")
    }
    
    @GetMapping("/get")
    fun getKey(@RequestParam key: String): ResponseEntity<Any> {
        val value = redisService.get(key)
        return if (value != null) {
            ResponseEntity.ok(value)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/delete")
    fun deleteKey(@RequestParam key: String): ResponseEntity<String> {
        redisService.delete(key)
        return ResponseEntity.ok("Key deleted successfully")
    }
    
    @GetMapping("/exists")
    fun hasKey(@RequestParam key: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(redisService.hasKey(key))
    }
} 