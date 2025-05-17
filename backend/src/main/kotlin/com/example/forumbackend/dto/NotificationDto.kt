// dto/NotificationDto.kt      (used by the Android app)
package com.example.forumbackend.dto

import com.example.forumbackend.entity.Notification
import java.time.LocalDateTime

data class NotificationDto(
    val id       : Long,
    val type     : String,          // "LIKE", "COMMENT"
    val source   : String,          // e.g. "Jason Chan"
    val snippet  : String,          // "liked your post about ..."
    val isUnread : Boolean,
    val createdAt: LocalDateTime
)

internal fun Notification.toDto() = NotificationDto(
    id        = id,
    type      = type,
    source    = source.displayName,
    snippet   = message,
    isUnread  = !seen,
    createdAt = createdAt
)
