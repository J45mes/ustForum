package com.example.forumbackend.controller

import com.example.forumbackend.dto.PostDto
import com.example.forumbackend.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/trending")
class TrendingController(
    private val postService: PostService
) {

    @GetMapping
    fun getTrendingPosts(): ResponseEntity<List<PostDto>> =
        ResponseEntity.ok(postService.getTrendingPosts())
}