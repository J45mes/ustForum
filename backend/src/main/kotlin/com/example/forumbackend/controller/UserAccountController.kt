package com.example.forumbackend.controller

import com.example.forumbackend.dto.ChangePasswordRequest
import com.example.forumbackend.dto.UpdateProfileRequest
import com.example.forumbackend.entity.UserInfo
import com.example.forumbackend.model.auth.AuthResponse
import com.example.forumbackend.security.JwtUtil
import com.example.forumbackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserAccountController(
    private val userSvc : UserService,
    private val encoder : PasswordEncoder,
    private val jwt     : JwtUtil
) {

    /* ---------------- PERSONAL INFO ---------------- */

    @PutMapping("{uid}/profile")
    fun updateProfile(
        @PathVariable uid: Long,
        @RequestBody  req: UpdateProfileRequest,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<AuthResponse> {

        val caller = jwt.extractUserId(auth.removePrefix("Bearer "))
        require(caller == uid) { "Forbidden" }

        val user = userSvc.updatePartial(uid, req)
        val newJwt = jwt.generateToken(user.userUid, user.loginName)

        return ResponseEntity.ok(
            AuthResponse(
                token       = newJwt,
                userUid     = user.userUid,
                username    = user.loginName,
                email       = user.email,
                displayName = user.displayName,
                major       = user.major
            )
        )
    }

    /* ---------------- PASSWORD --------------------- */

    @PutMapping("{uid}/password")
    fun changePassword(
        @PathVariable uid: Long,
        @RequestBody req: ChangePasswordRequest,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<Void> {
        val caller = jwt.extractUserId(auth.removePrefix("Bearer "))
        require(caller == uid) { "Forbidden" }
        userSvc.changePassword(uid, req, encoder)
        return ResponseEntity.ok().build()
    }
}
