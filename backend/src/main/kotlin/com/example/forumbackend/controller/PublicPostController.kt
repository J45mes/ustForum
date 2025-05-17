package com.example.forumbackend.controller

import com.example.forumbackend.dto.PostDto
import com.example.forumbackend.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PublicPostController(private val postService: PostService) {

    /* all posts */
    @GetMapping
    fun all(): ResponseEntity<List<PostDto>> =
        ResponseEntity.ok(postService.listAll())

    /* posts of one category (used by the Android app) */
    @GetMapping("/category/{cat}")
    fun byCategory(@PathVariable cat: String): ResponseEntity<List<PostDto>> =
        ResponseEntity.ok(postService.listByCategory(cat))
}