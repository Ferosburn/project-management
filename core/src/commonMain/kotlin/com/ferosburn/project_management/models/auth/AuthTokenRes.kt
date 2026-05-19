package com.ferosburn.project_management.models.auth

import com.ferosburn.project_management.models.user.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenRes(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val role: UserRole,
)
