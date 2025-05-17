package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val userUid: Long,
    val loginName: String,
    val displayName: String,
    val email: String
)
