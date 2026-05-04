package com.ferosburn.project_management.models.user

data class UserFilter(
    val limit: Int = 10,
    val page: Int = 1,
    val isActive: Boolean? = null,
    val role: UserRole? = null,
    val division: UserDivision? = null,
)
