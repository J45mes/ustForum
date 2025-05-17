package com.example.forumbackend.controller

import com.example.forumbackend.dto.NotificationDto
import com.example.forumbackend.security.JwtUtil
import com.example.forumbackend.service.NotificationService
import com.example.forumbackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users/{uid}/alerts")
class NotificationController(
    private val notifSvc: NotificationService,
    private val userSvc : UserService,
    private val jwt     : JwtUtil
) {

    @GetMapping
    fun list(
        @PathVariable uid: Long,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<List<NotificationDto>> {
        val caller = jwt.extractUserId(auth.removePrefix("Bearer "))
        require(caller == uid) { "Forbidden" }
        return ResponseEntity.ok(
            notifSvc.listForUser(userSvc.getUserById(uid))
        )
    }

    @PostMapping("{alertId}/seen")
    fun markSeen(
        @PathVariable alertId: Long,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<Void> {
        notifSvc.markSeen(alertId)
        return ResponseEntity.ok().build()
    }
}
