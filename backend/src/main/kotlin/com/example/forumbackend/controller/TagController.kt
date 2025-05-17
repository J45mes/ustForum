package com.example.forumbackend.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import com.example.forumbackend.repository.PostRepository
import com.example.forumbackend.service.PostService
@RestController
@RequestMapping("/api/tags")
class TagController(
    private val postService: PostService
) {
    @GetMapping
    fun autocompleteTags(@RequestParam("prefix") prefix: String): List<String> =
        postService.findTagsStartingWith(prefix)
}

