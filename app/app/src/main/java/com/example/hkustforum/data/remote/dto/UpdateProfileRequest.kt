// UpdateProfileRequest.kt
package com.example.hkustforum.data.remote.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val displayName: String,
    val username   : String
)
