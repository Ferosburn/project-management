@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.ferosburn.project_management.models.user.UserRole
import java.util.Date
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object JWTUtils {
    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private var accessExpiry: Long = 900
    private var refreshExpiry: Long = 604800

    fun init() {
        secret = System.getenv("JWT_SECRET") ?: error("JWT_SECRET environment variable not set")
        issuer = System.getenv("JWT_ISSUER") ?: error("JWT_ISSUER environment variable not set")
        audience = System.getenv("JWT_AUDIENCE") ?: error("JWT_AUDIENCE environment variable not set")
        accessExpiry = System.getenv("JWT_EXPIRY")?.toLong() ?: error("JWT_EXPIRY environment variable not set")
        refreshExpiry =
            System.getenv("JWT_REFRESH_EXPIRY")?.toLong() ?: error("JWT_REFRESH_EXPIRY environment variable not set")
    }

    fun generateAccessToken(userId: Uuid, role: UserRole): String {
        return JWT.create().withAudience(audience).withIssuer(issuer).withClaim("userId", userId.toString())
            .withClaim("role", role.name).withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + accessExpiry * 1000)).sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(userId: Uuid): String {
        return JWT.create().withAudience(audience).withIssuer(issuer).withClaim("userId", userId.toString())
            .withClaim("type", "refresh").withExpiresAt(Date(System.currentTimeMillis() + refreshExpiry * 1000))
            .sign(Algorithm.HMAC256(secret))
    }

    fun verifyRefreshToken(token: String): DecodedJWT? {
        return try {
            JWT.require(Algorithm.HMAC256(secret)).withAudience(audience).withIssuer(issuer)
                .withClaim("type", "refresh")      // ← ensures it's a refresh token
                .build().verify(token)
        } catch (_: JWTVerificationException) {
            null
        }
    }
}