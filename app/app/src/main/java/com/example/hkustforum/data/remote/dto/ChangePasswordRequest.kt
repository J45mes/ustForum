// ChangePasswordRequest.kt
package com.example.hkustforum.data.remote.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
