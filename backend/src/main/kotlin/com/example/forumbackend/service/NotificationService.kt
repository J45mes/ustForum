package com.example.forumbackend.service

import com.example.forumbackend.dto.NotificationDto
import com.example.forumbackend.dto.toDto
import com.example.forumbackend.entity.Notification
import com.example.forumbackend.entity.UserInfo
import com.example.forumbackend.repository.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val repo: NotificationRepository
) {

    fun listForUser(user: UserInfo): List<NotificationDto> =
        repo.findByTargetOrderByCreatedAtDesc(user).map { it.toDto() }

    @Transactional
    fun create(
        target : UserInfo,
        source : UserInfo,
        type   : String,
        message: String
    ) = repo.save(Notification(target = target, source = source,
        type = type, message = message))

    @Transactional
    fun markSeen(id: Long) {
        repo.findById(id).ifPresent { it.seen = true }
    }
}
