package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    val postName    : String,
    val content     : String,          // ← NEW
    val category    : String,
    val subCategory : String,
    val tag         : String? = null,
    val status      : String = "Active"
)
