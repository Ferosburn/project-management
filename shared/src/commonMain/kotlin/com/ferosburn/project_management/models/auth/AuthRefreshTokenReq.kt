package com.ferosburn.project_management.models.auth

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class AuthRefreshTokenReq(
    val refreshToken: String = "",
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (refreshToken.isBlank()) add("refresh token is required")
    }
}
