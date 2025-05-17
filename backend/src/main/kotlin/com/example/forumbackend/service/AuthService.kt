package com.example.forumbackend.service

import com.example.forumbackend.entity.UserInfo
import com.example.forumbackend.model.auth.*
import com.example.forumbackend.repository.UserInfoRepository
import com.example.forumbackend.security.JwtUtil
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AuthService(
    private val userRepo: UserInfoRepository,
    private val encoder : PasswordEncoder,
    private val jwt     : JwtUtil
) {

    /* ──────────────── LOGIN (e‑mail) ──────────────── */

    fun login(req: LoginRequest): AuthResponse {
        val user = userRepo.findByEmail(req.email.lowercase())
            ?: throw UsernameNotFoundException("User not found")

        if (!encoder.matches(req.password, user.password))
            throw BadCredentialsException("Invalid credentials")

        val token = jwt.generateToken(user.userUid, user.loginName)
        return user.toAuthResponse(token)
    }

    /* ──────────────── REGISTER ────────────────────── */

    fun register(req: RegisterRequest): AuthResponse {
        require(req.email.endsWith("@connect.ust.hk", ignoreCase = true)) {
            "E‑mail must be a HKUST connect address"
        }
        if (userRepo.existsByLoginName(req.username))
            throw IllegalArgumentException("Username already taken")
        if (userRepo.existsByEmail(req.email))
            throw IllegalArgumentException("E‑mail already registered")

        val user = UserInfo(
            displayName    = req.displayName,
            loginName      = req.username,
            email          = req.email.lowercase(),
            major          = req.major,
            password       = encoder.encode(req.password),
            registeredTime = LocalDateTime.now(),
            isAdmin        = false,
            status         = 1
        )

        val saved = userRepo.save(user)
        val token = jwt.generateToken(saved.userUid, saved.loginName)
        return saved.toAuthResponse(token)
    }

    /* ──────────────── helper ──────────────────────── */

    private fun UserInfo.toAuthResponse(jwt: String) =
        AuthResponse(
            token       = jwt,
            userUid     = userUid,
            username    = loginName,
            email       = email,
            displayName = displayName,
            major       = major
        )
}
