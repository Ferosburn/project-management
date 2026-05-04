package com.ferosburn.project_management.models.user

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class UserCreateReq(
    val name: String = "",
    val username: String = "",
    val password: String = "1sampai8",
    val role: String? = null,
    val division: String? = null,
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (name.isBlank()) add("name is required")
        if (username.isBlank()) add("username is required")
        when (role) {
            null -> add("role is required")
            !in UserRole.entries.map { it.name } -> add("role is invalid")
            in UserRole.entries.filter { it != UserRole.DEVELOPER }
                .map { it.name } if division != null -> add("division is exclusive for ${UserRole.DEVELOPER.name} role")

            UserRole.DEVELOPER.name if division == null -> add("division is required")
            UserRole.DEVELOPER.name if division != null && division !in UserDivision.entries.map { it.name } -> add("division is invalid")
            else -> {}
        }
    }
}
