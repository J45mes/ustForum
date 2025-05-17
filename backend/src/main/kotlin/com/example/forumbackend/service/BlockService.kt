package com.example.forumbackend.service

import com.example.forumbackend.dto.BlockDto
import com.example.forumbackend.dto.toBlockDto
import com.example.forumbackend.entity.Block
import com.example.forumbackend.repository.BlockRepository
import com.example.forumbackend.repository.PostRepository
import com.example.forumbackend.repository.UserInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BlockService(
    private val blockRepo: BlockRepository,
    private val postRepo: PostRepository,
    private val userRepo: UserInfoRepository,
    private val notifSvc  : NotificationService
) {

    fun listByPost(postId: Long): List<BlockDto> =
        blockRepo.findByPostPostId(postId).map { it.toBlockDto() }

    @Transactional
    fun addBlock(postId: Long, userUid: Long, content: String): BlockDto {
        val post = postRepo.findById(postId).orElseThrow()
        val user = userRepo.findById(userUid).orElseThrow()

        val saved = blockRepo.save(
            Block(
                content   = content,
                createdAt = LocalDateTime.now(),
                post      = post,
                author    = user,
                views     = 0
            )
        )
        if (user.userUid != post.author.userUid) {
            notifSvc.create(
                post.author,
                user,
                "COMMENT",
                "${user.displayName} commented on your post about \"${post.content.take(30)}…\""
            )
        }
        return saved.toBlockDto()
    }

    @Transactional
    fun deleteBlock(blockId: Long, userUid: Long) {
        val block = blockRepo.findById(blockId).orElseThrow()
        require(block.author.userUid == userUid) { "Forbidden" }
        blockRepo.delete(block)
    }
}