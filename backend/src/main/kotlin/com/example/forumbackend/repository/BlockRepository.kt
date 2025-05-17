package com.example.forumbackend.repository

import com.example.forumbackend.entity.Block
import com.example.forumbackend.entity.Post
import com.example.forumbackend.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlockRepository : JpaRepository<Block, Long> {
    fun findByPost(post: Post): List<Block>
    fun findByPostPostId(postId: Long): List<Block>
    fun findByAuthor(author: UserInfo): List<Block>
}