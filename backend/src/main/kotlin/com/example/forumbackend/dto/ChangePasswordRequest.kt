// dto/ChangePasswordRequest.kt
package com.example.forumbackend.dto

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
