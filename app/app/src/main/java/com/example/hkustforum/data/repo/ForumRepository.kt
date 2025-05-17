package com.example.hkustforum.data.repo

import com.example.hkustforum.data.local.TokenDataStore
import com.example.hkustforum.data.remote.ForumApi
import com.example.hkustforum.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForumRepository @Inject constructor(
    private val api: ForumApi,
    private val ds : TokenDataStore
) {

    /* ─────── AUTH ───────────────────────────────────────────── */

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        val res = api.login(LoginRequest(email, password))
        ds.save(res.token, res.userUid, res.displayName, res.major)
        res
    }

    suspend fun register(req: RegisterRequest) = withContext(Dispatchers.IO) {
        val res = api.register(req)
        ds.save(res.token, res.userUid, res.displayName, res.major)
        res
    }

    /* ─────── POSTS & BLOCKS (unchanged) ─────────────────────── */

    suspend fun listPosts()          = api.getAllPosts()
    suspend fun listBlocks(id: Long) = api.listBlocks(id)
    suspend fun addBlock(id: Long, txt: String) =
        api.addBlock(id, BlockRequest(txt))

    suspend fun like(id: Long)    = api.like(id)
    suspend fun dislike(id: Long) = api.dislike(id)

    /* ─────── CREATE POST (needs uid) ────────────────────────── */

    suspend fun createPost(req: CreatePostRequest) = withContext(Dispatchers.IO) {
        val uid = ds.uid.firstOrNull()
            ?: error("User id missing – did you log in?")
        api.createPost(uid, req)
    }

    /* ---------- profile ---------- */
    suspend fun updateProfile(name: String, username: String) = withContext(Dispatchers.IO) {
        val uid = ds.uid.firstOrNull() ?: error("Not logged in")
        val res = api.updateProfile(uid, UpdateProfileRequest(name, username))
        ds.save(res.token, res.userUid, res.displayName, res.major)
        res
    }

    suspend fun changePassword(old: String, new: String) = withContext(Dispatchers.IO) {
        val uid = ds.uid.firstOrNull() ?: error("Not logged in")
        api.changePassword(uid, ChangePasswordRequest(old, new))
    }

    /* ---------- self posts ---------- */
    suspend fun listOwnPosts() = withContext(Dispatchers.IO) {
        val uid = ds.uid.firstOrNull() ?: error("Not logged in")
        api.listOwnPosts(uid)
    }

    /* ---------- alerts ---------- */
    suspend fun listAlerts() = withContext(Dispatchers.IO) {
        val uid = ds.uid.firstOrNull() ?: error("Not logged in")
        api.listAlerts(uid)
    }

    suspend fun markAlertSeen(id: Long) = withContext(Dispatchers.IO) {
        val uid = ds.uid.firstOrNull() ?: error("Not logged in")
        api.markAlertSeen(uid, id)
    }
    suspend fun autocompleteTags(prefix: String): List<String> =
        api.autocompleteTags(prefix)
    /* expose flows */
    val tokenFlow = ds.token
    val uidFlow   = ds.uid
    val nameFlow  = ds.displayName
    val majorFlow = ds.major
}
