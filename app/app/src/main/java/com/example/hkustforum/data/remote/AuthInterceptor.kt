package com.example.hkustforum.data.remote

import com.example.hkustforum.data.local.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req  = chain.request()
        val path = req.url.encodedPath   // e.g. "/api/auth/login"

        // 1) Skip auth endpoints
        if (path.startsWith("/api/auth/")) {
            return chain.proceed(req)
        }

        // 2) Block-only fetch stored token
        val token = runBlocking { tokenStore.token.firstOrNull() }

        // 3) Add header if available
        val authed = if (token.isNullOrBlank()) req else req.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authed)
    }
}
