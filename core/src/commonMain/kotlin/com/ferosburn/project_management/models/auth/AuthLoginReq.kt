package com.ferosburn.project_management.models.auth

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class AuthLoginReq(
    val username: String = "",
    val password: String = "",
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (username.isBlank()) add("username is required")
        if (password.isBlank()) add("password is required")
        if (password.isNotBlank() && password.length < 8) add("password must be at least 8 characters long")
    }
}