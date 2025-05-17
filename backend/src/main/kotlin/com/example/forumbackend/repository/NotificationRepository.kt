package com.example.forumbackend.repository

import com.example.forumbackend.entity.Notification
import com.example.forumbackend.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByTargetOrderByCreatedAtDesc(target: UserInfo): List<Notification>
}
