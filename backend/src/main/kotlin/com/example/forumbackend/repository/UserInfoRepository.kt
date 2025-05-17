package com.example.forumbackend.repository

import com.example.forumbackend.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Long> {

    /* ───────── read ───────── */

    fun findByLoginName(loginName: String): UserInfo?
    fun findByEmail(email: String): UserInfo?

    /* ───────── existence ──── */

    fun existsByLoginName(loginName: String): Boolean
    fun existsByEmail(email: String): Boolean
}