package com.example.forumbackend.controller

import com.example.forumbackend.security.JwtUtil
import com.example.forumbackend.service.LikeDislikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class LikeDislikeController(
    private val likeService: LikeDislikeService,
    private val jwt: JwtUtil
) {

    /* ---------- react ---------- */

    @PostMapping("/{postId}/like")
    fun like(
        @PathVariable postId: Long,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<Void> {
        val uid = jwt.extractUserId(auth.removePrefix("Bearer "))
        likeService.likeOrDislikePost(postId, uid, true)
        return ResponseEntity.ok().build()          // <-- empty 200 body
    }

    @PostMapping("/{postId}/dislike")
    fun dislike(
        @PathVariable postId: Long,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<Void> {
        val uid = jwt.extractUserId(auth.removePrefix("Bearer "))
        likeService.likeOrDislikePost(postId, uid, false)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{postId}/like-dislike")
    fun remove(
        @PathVariable postId: Long,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<Void> {
        val uid = jwt.extractUserId(auth.removePrefix("Bearer "))
        likeService.removeLikeDislike(postId, uid)
        return ResponseEntity.noContent().build()
    }

    /* ---------- counters ---------- */

    @GetMapping("/{postId}/likes-count")
    fun likes(@PathVariable postId: Long): ResponseEntity<Long> =
        ResponseEntity.ok(likeService.getPostLikesCount(postId))

    @GetMapping("/{postId}/dislikes-count")
    fun dislikes(@PathVariable postId: Long): ResponseEntity<Long> =
        ResponseEntity.ok(likeService.getPostDislikesCount(postId))
}