package com.ferosburn.project_management.models.auth

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class AuthChangePasswordReq(
    val oldPassword: String = "",
    val newPassword: String = "",
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (oldPassword.isBlank()) add("oldPassword is required")
        if (oldPassword.isNotBlank() && oldPassword.length < 8) add("oldPassword must be at least 8 characters long")
        if (newPassword.isBlank()) add("newPassword is required")
        if (newPassword.isNotBlank() && newPassword.length < 8) add("newPassword must be at least 8 characters long")
    }
}
