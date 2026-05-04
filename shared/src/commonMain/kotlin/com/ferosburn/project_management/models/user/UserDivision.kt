package com.ferosburn.project_management.models.user

import kotlinx.serialization.Serializable

@Serializable
enum class UserDivision {
    FRONTEND, UI, BACKEND, MOBILE, DEVOPS,
}