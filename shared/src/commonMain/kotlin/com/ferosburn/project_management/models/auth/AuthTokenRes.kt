package com.ferosburn.project_management.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenRes(
    val accessToken: String,
    val refreshToken: String,
)
