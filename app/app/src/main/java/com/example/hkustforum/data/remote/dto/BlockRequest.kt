package com.example.hkustforum.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlockRequest(
    val content: String
)
