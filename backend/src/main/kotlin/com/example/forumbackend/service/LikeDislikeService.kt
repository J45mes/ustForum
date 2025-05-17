package com.example.forumbackend.service

import com.example.forumbackend.entity.LikeDislikeHistory
import com.example.forumbackend.repository.LikeDislikeRepository
import com.example.forumbackend.repository.PostRepository
import com.example.forumbackend.repository.UserInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LikeDislikeService(
    private val repo: LikeDislikeRepository,
    private val postRepo: PostRepository,
    private val userRepo: UserInfoRepository,
    private val notifSvc  : NotificationService
) {

    /* ---------- public API used by LikeDislikeController ---------- */

    @Transactional
    fun likeOrDislikePost(postId: Long, userId: Long, like: Boolean): LikeDislikeHistory {
        val post   = postRepo.findById(postId).orElseThrow()
        val source = userRepo.findById(userId).orElseThrow()
        val target = post.author

        val existing = repo.findByPostAndUser(post, source)
        val result: LikeDislikeHistory

        if (existing == null) {
            result = repo.save(
                LikeDislikeHistory(
                    post        = post,
                    user        = source,
                    likeDislike = like,
                    createTime  = LocalDateTime.now()
                )
            )
            if (like && source.userUid != target.userUid) {
                notifSvc.create(
                    target,
                    source,
                    "LIKE",
                    "${source.displayName} liked your post: \"${post.content.take(30)}…\""
                )
            }
        } else {
            if (existing.likeDislike == like) {
                repo.delete(existing)
                result = existing
            } else {
                result = repo.save(
                    LikeDislikeHistory(
                        id          = existing.id,
                        post        = post,
                        user        = source,
                        likeDislike = like,
                        createTime  = existing.createTime
                    )
                )
            }
        }

        return result
    }

    @Transactional
    fun removeLikeDislike(postId: Long, userId: Long) {
        val post = postRepo.findById(postId).orElseThrow()
        val user = userRepo.findById(userId).orElseThrow()
        repo.findByPostAndUser(post, user)?.let { repo.delete(it) }
    }

    fun getPostLikesCount(postId: Long): Long =
        repo.countByPostAndLikeDislike(postRepo.getReferenceById(postId), true)

    fun getPostDislikesCount(postId: Long): Long =
        repo.countByPostAndLikeDislike(postRepo.getReferenceById(postId), false)
}