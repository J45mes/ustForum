// src/main/kotlin/com/example/forumbackend/entity/User.kt
package com.example.forumbackend.entity

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "user_info")
data class UserInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_uid")
    val userUid: Long = 0,

    @Column(name = "display_name")
    val displayName: String = "",

    @Column(name = "login_name", unique = true)
    val loginName: String = "",

    val password: String = "",

    @Column(name = "email", unique = true)
    val email: String = "",

    @Column(name = "registered_time")
    val registeredTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_admin")
    val isAdmin: Boolean = false,

    val status: Int = 0,

    val school: String = "",

    val major: String = "",

    @Column(name = "graduate_year")
    val graduateYear: LocalDateTime? = null
)

@Entity
@Table(name = "post")
data class Post(

    /* ── identifiers ─────────────────────────────────────────────── */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    val postId: Long = 0,

    /* ── user‑supplied data ──────────────────────────────────────── */
    @Column(name = "post_name", nullable = false)
    val postName: String,

    /** NEW — the real body text of the post (was “first block”). */
    @Lob
    @Column(name = "content", nullable = false)
    val content: String,

    var tag: String? = null,

    val category: String,

    @Column(name = "sub_category")
    val subCategory: String,

    /* ── server‑managed data ─────────────────────────────────────── */
    @Column(name = "last_update_time")
    val lastUpdateTime: LocalDateTime = LocalDateTime.now(),

    var views: Long = 0,

    val status: String,

    @Column(name = "is_private")
    val isPrivate: Boolean = false,

    /* ── relations ──────────────────────────────────────────────── */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    val author: UserInfo,

    /** Comments are still stored as blocks. */
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val blocks: List<Block> = emptyList()
)

@Entity
@Table(name = "block")
data class Block(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    val blockId: Long = 0,

    @Lob
    @Column(name = "info", nullable = false)
    val content: String = "",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "views")
    val views: Long = 0,

    /* relations ---------------------------------------------------- */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    val author: UserInfo
)
@Entity
@Table(
    name = "like_dislike_history",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_post_user", columnNames = ["post_id", "user_uid"])
    ]
)
class LikeDislikeHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)          // post_id
    @JoinColumn(name = "post_id", nullable = false)
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)          // user_uid
    @JoinColumn(name = "user_uid", nullable = false)
    val user: UserInfo,

    @Column(name = "like_dislike", nullable = false)
    val likeDislike: Boolean,                   // true = like, false = dislike

    @Column(name = "create_time", nullable = false)
    val createTime: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "user_preference")
data class UserPreference(
    @Id
    val userUid: Long = 0,

    val visualTheme: Int = 0
)