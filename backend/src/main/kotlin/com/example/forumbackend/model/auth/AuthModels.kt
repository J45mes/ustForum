// src/main/kotlin/com/example/forumbackend/model/auth/AuthModels.kt
package com.example.forumbackend.model.auth

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username    : String,
    val email       : String,
    val password    : String,
    val displayName : String,
    val major       : String
)

data class AuthResponse(
    val token      : String,
    val userUid    : Long,
    val username   : String,
    val email      : String,
    val displayName: String,
    val major      : String?          // nullable because legacy accounts may not have it
)

