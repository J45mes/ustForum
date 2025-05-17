// src/main/kotlin/com/example/forumbackend/controller/UserPreferenceController.kt
package com.example.forumbackend.controller

import com.example.forumbackend.entity.UserPreference
import com.example.forumbackend.service.UserPreferenceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user-preferences")
class UserPreferenceController(private val userPreferenceService: UserPreferenceService) {

    @GetMapping("/{userUid}")
    fun getUserPreference(@PathVariable userUid: Long): ResponseEntity<UserPreference> {
        val preference = userPreferenceService.getUserPreference(userUid)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(preference)
    }

    @PutMapping("/{userUid}")
    fun updateUserPreference(
        @PathVariable userUid: Long,
        @RequestBody preference: UserPreference
    ): ResponseEntity<UserPreference> {
        val updatedPreference = userPreferenceService.saveUserPreference(
            preference.copy(userUid = userUid)
        )
        return ResponseEntity.ok(updatedPreference)
    }

    @PatchMapping("/{userUid}/theme")
    fun updateVisualTheme(
        @PathVariable userUid: Long,
        @RequestParam visualTheme: Int
    ): ResponseEntity<UserPreference> {
        val updatedPreference = userPreferenceService.updateVisualTheme(userUid, visualTheme)
        return ResponseEntity.ok(updatedPreference)
    }
}