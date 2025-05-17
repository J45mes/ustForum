package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDto(
    val postId        : Long,
    val postName      : String,
    val content       : String,   // ← NEW
    val category      : String,
    val subCategory   : String,
    val tag           : String?,
    val author        : UserDto,
    val views         : Long,
    val status        : String,
    val likes         : Int,
    val dislikes      : Int,
    val lastUpdateTime: String
)
