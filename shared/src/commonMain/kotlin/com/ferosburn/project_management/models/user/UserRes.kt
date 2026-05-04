package com.ferosburn.project_management.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRes(
    val id: String,
    val name: String,
    val username: String,
    val role: UserRole,
    val division: UserDivision?,
    val isActive: Boolean,
)