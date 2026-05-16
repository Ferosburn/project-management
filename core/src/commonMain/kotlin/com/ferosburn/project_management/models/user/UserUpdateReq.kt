package com.ferosburn.project_management.models.user

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateReq(
    val name: String? = null,
    val role: String? = null,
    val division: String? = null,
    val isActive: Boolean? = null,
) : Validatable {
    override fun validate(): List<String> = buildList {
        when (role) {
            !in UserRole.entries.map { it.name } -> add("role is invalid")
            in UserRole.entries.filter { it != UserRole.DEVELOPER }
                .map { it.name } if division != null -> add("division is exclusive for ${UserRole.DEVELOPER.name}")
            UserRole.DEVELOPER.name if division != null && division !in UserDivision.entries.map { it.name } -> add("division is invalid")
            else -> {}
        }
    }
}
