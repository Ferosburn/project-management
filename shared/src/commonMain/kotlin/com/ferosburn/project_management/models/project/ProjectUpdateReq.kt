package com.ferosburn.project_management.models.project

import kotlinx.serialization.Serializable

@Serializable
data class ProjectUpdateReq(
    val name: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null,
)
