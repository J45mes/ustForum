package com.example.forumbackend.service

import com.example.forumbackend.dto.UpdateProfileRequest
import com.example.forumbackend.entity.UserInfo
import com.example.forumbackend.dto.ChangePasswordRequest
import com.example.forumbackend.repository.UserInfoRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserInfoRepository
) {
    // Method to get all users
    fun getAllUsers(): List<UserInfo> {
        return userRepository.findAll()
    }

    // Method to get a user by ID
    fun getUserById(id: Long): UserInfo {
        return userRepository.findById(id).orElseThrow {
            NoSuchElementException("User not found with id: $id")
        }
    }

    // Method to get user by username
    fun getUserByUsername(username: String): UserInfo {
        return userRepository.findByLoginName(username)
            ?: throw NoSuchElementException("User not found with username: $username")
    }

    // Method to get user by email
    fun getUserByEmail(email: String): UserInfo {
        return userRepository.findByEmail(email)
            ?: throw NoSuchElementException("User not found with email: $email")
    }

    // Method to create a new user
    fun createUser(user: UserInfo): UserInfo {
        // Check if username already exists
        if (userRepository.findByLoginName(user.loginName) != null) {
            throw IllegalArgumentException("Username is already taken")
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("Email is already registered")
        }

        // Set registration time if not already set
        val userToSave = if (user.registeredTime == null) {
            user.copy(registeredTime = LocalDateTime.now())
        } else {
            user
        }

        return userRepository.save(userToSave)
    }

    // Method to update an existing user
    fun updateUser(id: Long, updatedUser: UserInfo): UserInfo {
        val existingUser = getUserById(id)

        // Check if the new username is already taken by someone else
        val userWithSameUsername = userRepository.findByLoginName(updatedUser.loginName)
        if (userWithSameUsername != null && userWithSameUsername.userUid != id) {
            throw IllegalArgumentException("Username is already taken")
        }

        // Check if the new email is already taken by someone else
        val userWithSameEmail = userRepository.findByEmail(updatedUser.email)
        if (userWithSameEmail != null && userWithSameEmail.userUid != id) {
            throw IllegalArgumentException("Email is already registered")
        }

        // Update the user info but preserve original userUid and registeredTime
        val userToUpdate = updatedUser.copy(
            userUid = existingUser.userUid,
            registeredTime = existingUser.registeredTime
        )

        return userRepository.save(userToUpdate)
    }

    // Method to delete a user
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NoSuchElementException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }
    fun updatePartial(id: Long, req: UpdateProfileRequest): UserInfo {
        val u = getUserById(id)
        if (req.username != u.loginName && userRepository.existsByLoginName(req.username))
            throw IllegalArgumentException("Username taken")

        return userRepository.save(
            u.copy(displayName = req.displayName, loginName = req.username)
        )
    }
    fun changePassword(
        id: Long,
        req: ChangePasswordRequest,
        encoder: PasswordEncoder
    ) {
        val u = getUserById(id)
        require(encoder.matches(req.oldPassword, u.password)) { "Old password wrong" }
        userRepository.save(u.copy(password = encoder.encode(req.newPassword)))
    }
}