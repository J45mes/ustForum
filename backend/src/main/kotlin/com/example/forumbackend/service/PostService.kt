package com.example.forumbackend.service

import com.example.forumbackend.dto.CreatePostRequest
import com.example.forumbackend.dto.PostDto
import com.example.forumbackend.dto.toUserDto
import com.example.forumbackend.entity.Post
import com.example.forumbackend.repository.LikeDislikeRepository
import com.example.forumbackend.repository.PostRepository
import com.example.forumbackend.repository.UserInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PostService(
    private val postRepo : PostRepository,
    private val likeRepo : LikeDislikeRepository,
    private val userRepo : UserInfoRepository
) {

    /* ─────────── queries ─────────── */

    fun listAll()                    = postRepo.findAll().map { it.toDto() }
    fun getOne(id: Long)             = postRepo.findById(id).orElseThrow().toDto()
    fun listByAuthor(uid: Long)      =
        postRepo.findByAuthor(userRepo.getReferenceById(uid)).map { it.toDto() }
    fun listByCategory(cat: String)  = postRepo.findByCategory(cat).map { it.toDto() }
    fun getTrendingPosts()           = postRepo.findTop5ByOrderByViewsDesc().map { it.toDto() }

    /* ─────────── create ─────────── */

    @Transactional
    fun create(authorUid: Long, req: CreatePostRequest): PostDto {
        val author = userRepo.findById(authorUid).orElseThrow()
        val extractedTag: String? = Regex("#([\\w ]+)")
            .find(req.content)
            ?.groupValues
            ?.get(1)
            ?.trim()

        val post = postRepo.save(
            Post(
                postName    = req.postName,
                content     = req.content,          // ← NEW
                tag         = extractedTag,
                category    = req.category,
                subCategory = req.subCategory,
                status      = req.status,
                author      = author,
                isPrivate   = false
            )
        )

        return post.toDto()
    }

    /* ─────────── misc helpers ─────────── */

    @Transactional
    fun increaseViews(postId: Long) =
        postRepo.findById(postId).ifPresent { it.views++ }

    private fun Post.toDto() = PostDto(
        postId         = postId,
        postName       = postName,
        content        = content,                 // ← NEW
        category       = category,
        subCategory    = subCategory,
        tag            = this.tag?.takeIf { it.isNotBlank() },
        author         = author.toUserDto(),
        views          = views,
        status         = status,
        likes          = likeRepo.countByPostAndLikeDislike(this, true).toInt(),
        dislikes       = likeRepo.countByPostAndLikeDislike(this, false).toInt(),
        lastUpdateTime = lastUpdateTime
    )
    @Transactional(readOnly = true)
    fun findTagsStartingWith(prefix: String): List<String> =
        postRepo.findDistinctTagsLike("$prefix%")
}
