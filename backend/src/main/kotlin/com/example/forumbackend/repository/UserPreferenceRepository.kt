// src/main/kotlin/com/example/forumbackend/repository/UserPreferenceRepository.kt
package com.example.forumbackend.repository

import com.example.forumbackend.entity.UserPreference
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPreferenceRepository : JpaRepository<UserPreference, Long>