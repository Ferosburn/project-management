package com.ferosburn.project_management.models.project

import kotlinx.serialization.Serializable

@Serializable
data class ProjectFilter(
    val limit: Int = 10,
    val page: Int = 1,
    val isActive: Boolean? = null,
)
