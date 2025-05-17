package com.example.forumbackend.repository

import com.example.forumbackend.entity.LikeDislikeHistory
import com.example.forumbackend.entity.Post
import com.example.forumbackend.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository

interface LikeDislikeRepository : JpaRepository<LikeDislikeHistory, Long> {

    fun findByPostAndUser(post: Post, user: UserInfo): LikeDislikeHistory?

    fun countByPostAndLikeDislike(post: Post, like: Boolean): Long
}