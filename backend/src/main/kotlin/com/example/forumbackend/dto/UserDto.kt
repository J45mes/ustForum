package com.example.forumbackend.dto

import com.example.forumbackend.entity.UserInfo

/**
 * MUST be identical to
 * com.example.forum.data.remote.dto.UserDto (Android module)
 */
data class UserDto(
    val userUid: Long,
    val loginName: String,
    val displayName: String,
    val email: String
)

internal fun UserInfo.toUserDto() = UserDto(
    userUid     = userUid,
    loginName   = loginName,
    displayName = displayName,
    email       = email
)