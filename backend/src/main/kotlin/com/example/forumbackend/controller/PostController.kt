package com.example.forumbackend.controller

import com.example.forumbackend.dto.CreatePostRequest
import com.example.forumbackend.dto.PostDto
import com.example.forumbackend.security.JwtUtil
import com.example.forumbackend.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users/{authorId}/posts")
class PostController(
    private val postService: PostService,
    private val jwt: JwtUtil
) {

    /* -------- list posts of one author -------- */
    @GetMapping
    fun list(@PathVariable authorId: Long): ResponseEntity<List<PostDto>> =
        ResponseEntity.ok(postService.listByAuthor(authorId))

    /* -------- single post -------- */
    @GetMapping("/{postId}")
    fun getOne(
        @PathVariable authorId: Long,   // kept for URL symmetry
        @PathVariable postId: Long
    ): ResponseEntity<PostDto> =
        ResponseEntity.ok(postService.getOne(postId))

    /* -------- create post (token required) -------- */
    @PostMapping
    fun create(
        @PathVariable authorId: Long,
        @RequestBody  req: CreatePostRequest,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<PostDto> {
        /* authorisation: only the user itself can create via this URL   */
        val callerUid = jwt.extractUserId(auth.removePrefix("Bearer "))
        require(callerUid == authorId) { "Forbidden" }

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(postService.create(authorId, req))
    }
}