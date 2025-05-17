// src/main/kotlin/com/example/forumbackend/security/CustomUserDetailsService.kt
package com.example.forumbackend.security

import com.example.forumbackend.repository.UserInfoRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserInfoRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByLoginName(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        return User.builder()
            .username(user.loginName)
            .password(user.password)
            .roles(if (user.isAdmin) "ADMIN" else "USER")
            .build()
    }
}

