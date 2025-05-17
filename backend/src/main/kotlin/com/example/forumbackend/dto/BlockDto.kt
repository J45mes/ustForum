package com.example.forumbackend.dto

import com.example.forumbackend.entity.Block
import java.time.LocalDateTime

data class BlockDto(
    val blockId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val views: Long,
    val author: UserDto
)

internal fun Block.toBlockDto() = BlockDto(
    blockId   = blockId,
    content   = content,            // <-- matches entity field
    createdAt = createdAt,
    views     = views,
    author    = author.toUserDto()
)