// AlertDto.kt
package com.example.hkustforum.data.remote.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertDto(
    val id       : Long,
    val type     : String,
    val source   : String,
    val snippet  : String,
    val isUnread : Boolean,
    val createdAt: String
)
