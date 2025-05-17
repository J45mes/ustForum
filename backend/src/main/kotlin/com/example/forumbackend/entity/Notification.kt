// entity/Notification.kt
package com.example.forumbackend.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notification")
data class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** OWNER – the one who will read the alert */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "target_uid")
    val target: UserInfo,

    /** ACTOR – the one who triggered it */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "source_uid")
    val source: UserInfo,

    val type: String,                // "LIKE" / "COMMENT"

    @Lob val message: String,        // “Jason liked your post about …”
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var seen: Boolean = false
)
