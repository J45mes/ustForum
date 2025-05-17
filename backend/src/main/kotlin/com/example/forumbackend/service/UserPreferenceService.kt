// src/main/kotlin/com/example/forumbackend/service/UserPreferenceService.kt
package com.example.forumbackend.service

import com.example.forumbackend.entity.UserPreference
import com.example.forumbackend.repository.UserPreferenceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserPreferenceService(private val userPreferenceRepository: UserPreferenceRepository) {

    fun getUserPreference(userUid: Long): UserPreference? =
        userPreferenceRepository.findById(userUid).orElse(null)

    @Transactional
    fun saveUserPreference(userPreference: UserPreference): UserPreference =
        userPreferenceRepository.save(userPreference)

    @Transactional
    fun updateVisualTheme(userUid: Long, visualTheme: Int): UserPreference {
        val preference = getUserPreference(userUid)

        return if (preference != null) {
            userPreferenceRepository.save(preference.copy(visualTheme = visualTheme))
        } else {
            userPreferenceRepository.save(UserPreference(userUid = userUid, visualTheme = visualTheme))
        }
    }
}