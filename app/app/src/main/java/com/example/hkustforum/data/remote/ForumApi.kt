package com.example.hkustforum.data.remote

import com.example.hkustforum.data.remote.dto.*
import retrofit2.http.*

interface ForumApi {

    /* ───── Auth ───── */

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse


    /* ───── Public posts ───── */

    @GET("api/posts")
    suspend fun getAllPosts(): List<PostDto>

    @GET("api/posts/{postId}/blocks")
    suspend fun listBlocks(@Path("postId") postId: Long): List<BlockDto>

    @POST("api/posts/{postId}/blocks")
    suspend fun addBlock(
        @Path("postId") postId: Long,
        @Body req: BlockRequest
    ): BlockDto

    @POST("api/posts/{postId}/like")
    suspend fun like(@Path("postId") postId: Long)

    @POST("api/posts/{postId}/dislike")
    suspend fun dislike(@Path("postId") postId: Long)


    /* ───── Author‑scoped posts (requires uid in path!) ───── */

    @POST("api/users/{authorId}/posts")
    suspend fun createPost(
        @Path("authorId") authorId: Long,
        @Body             req:      CreatePostRequest
    ): PostDto

    /* ---- account ---- */
    @PUT("api/users/{uid}/profile")
    suspend fun updateProfile(
        @Path("uid") uid: Long,
        @Body req: UpdateProfileRequest
    ): AuthResponse          // returns fresh JWT & user data

    @PUT("api/users/{uid}/password")
    suspend fun changePassword(
        @Path("uid") uid: Long,
        @Body req: ChangePasswordRequest
    ): Unit

    /* ---- self posts ---- */
    @GET("api/users/{uid}/posts")
    suspend fun listOwnPosts(@Path("uid") uid: Long): List<PostDto>

    /* ---- alerts ---- */
    @GET("api/users/{uid}/alerts")
    suspend fun listAlerts(@Path("uid") uid: Long): List<AlertDto>

    @POST("api/users/{uid}/alerts/{alertId}/seen")
    suspend fun markAlertSeen(
        @Path("uid") uid: Long,
        @Path("alertId") alertId: Long
    ): Unit

    @GET("api/tags")
    suspend fun autocompleteTags(@Query("prefix") prefix: String): List<String>
}
