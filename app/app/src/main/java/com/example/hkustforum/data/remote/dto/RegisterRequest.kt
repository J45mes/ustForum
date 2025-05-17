package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val username    : String,
    val email       : String,
    val password    : String,
    val displayName : String,
    val major       : String
)
