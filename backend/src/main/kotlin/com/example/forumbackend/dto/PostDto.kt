package com.example.forumbackend.dto

import java.time.LocalDateTime

/**
 * Mirrors the PostDto the Android client already ships with.
 */
data class PostDto(
    val postId        : Long,
    val postName      : String,
    val content       : String,              // ← NEW
    val category      : String,
    val subCategory   : String,
    val tag           : String?,             // nullable
    val author        : UserDto,
    val views         : Long,
    val status        : String,
    val likes         : Int,
    val dislikes      : Int,
    val lastUpdateTime: LocalDateTime
)