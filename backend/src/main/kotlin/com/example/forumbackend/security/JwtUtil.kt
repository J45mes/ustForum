package com.example.forumbackend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Function

@Component
class JwtUtil(

    /** 256-bit (or longer) base-64 secret from application.yml / properties */
    @Value("\${jwt.secret:ZGVmYXVsdFNlY3JldEtleVdpdGhNdXN0QmVBdExlYXN0MzJCeXRlc0xvbmc=}")
    secret: String,

    /** token lifetime in milliseconds (default: 24 h) */
    @Value("\${jwt.expiration:86400000}")
    private val jwtExpirationMs: Long
) {

    /* ------------------------------------------------------------------ */
    /* Sign-/verify key                                                   */
    /* ------------------------------------------------------------------ */

    private val secretKey: Key =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

    /* ------------------------------------------------------------------ */
    /*  Public API                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Create a JWT that contains the username as `sub` and the numeric user‐id
     * as a custom claim.  The token is valid for [jwtExpirationMs] ms.
     */
    fun generateToken(userId: Long, username: String): String {
        val now  = Instant.now()                         // UTC
        val exp  = now.plusMillis(jwtExpirationMs)

        val claims: Map<String, Any> = mapOf("userId" to userId)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String =
        extractClaim(token, Claims::getSubject)

    fun extractUserId(token: String): Long =
        (extractAllClaims(token)["userId"] as Number).toLong()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        userDetails.username == extractUsername(token) && !isTokenExpired(token)

    /* ------------------------------------------------------------------ */
    /*  Internal helpers                                                  */
    /* ------------------------------------------------------------------ */

    private fun <T> extractClaim(token: String, resolver: Function<Claims, T>): T =
        resolver.apply(extractAllClaims(token))

    private fun extractAllClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

    private fun isTokenExpired(token: String): Boolean =
        extractExpiration(token).before(Date())

    private fun extractExpiration(token: String): Date =
        extractClaim(token, Claims::getExpiration)
}