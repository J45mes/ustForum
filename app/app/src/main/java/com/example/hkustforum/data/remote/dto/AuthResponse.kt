package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val token      : String,
    val userUid    : Long,
    val username   : String,
    val email      : String,
    val displayName: String,
    val major      : String?
)
