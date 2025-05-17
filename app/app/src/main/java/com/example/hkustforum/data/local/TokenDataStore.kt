package com.example.hkustforum.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ds by preferencesDataStore("auth")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    private val KEY_JWT   = stringPreferencesKey("jwt")
    private val KEY_UID   = stringPreferencesKey("uid")
    private val KEY_NAME  = stringPreferencesKey("name")
    private val KEY_MAJOR = stringPreferencesKey("major")

    val token      : Flow<String?> = ctx.ds.data.map { it[KEY_JWT] }
    val uid        : Flow<Long?>   = ctx.ds.data.map { it[KEY_UID]?.toLongOrNull() }
    val displayName: Flow<String?> = ctx.ds.data.map { it[KEY_NAME] }
    val major      : Flow<String?> = ctx.ds.data.map { it[KEY_MAJOR] }

    /** Save whatever fields we currently have. */
    suspend fun save(jwt: String, uid: Long? = null, name: String? = null, major: String? = null) =
        ctx.ds.edit {
            it[KEY_JWT] = jwt
            uid?.let   { v -> it[KEY_UID]   = v.toString() }
            name?.let  { v -> it[KEY_NAME]  = v }
            major?.let { v -> it[KEY_MAJOR] = v }
        }

    suspend fun clear() = ctx.ds.edit {
        it.remove(KEY_JWT); it.remove(KEY_UID); it.remove(KEY_NAME); it.remove(KEY_MAJOR)
    }
}
