package com.ferosburn.project_management.models.user

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    DEVELOPER, MANAGER, SUPERADMIN, CLIENT,
}