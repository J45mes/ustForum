package com.example.forumbackend.dto

/**
 * Payload that the *client* sends when it wants to create a post.
 * Do NOT put server-managed fields (author, id, timestamps) in here.
 */
data class CreatePostRequest(
    val postName    : String,
    val content     : String,   // ← NEW
    val category    : String,
    val subCategory : String,
    val tag         : String? = null,
    val status      : String
)