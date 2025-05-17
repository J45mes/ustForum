// src/main/kotlin/com/example/forumbackend/controller/AuthController.kt
package com.example.forumbackend.controller

import com.example.forumbackend.model.auth.AuthResponse
import com.example.forumbackend.model.auth.LoginRequest
import com.example.forumbackend.model.auth.RegisterRequest
import com.example.forumbackend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        try {
            val authResponse = authService.login(request)
            return ResponseEntity.ok(authResponse)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        try {
            val authResponse = authService.register(request)
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

