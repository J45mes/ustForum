package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlockDto(
    val blockId: Long,
    val content: String,
    val createdAt: String,
    val views: Long,
    val author: UserDto
)
