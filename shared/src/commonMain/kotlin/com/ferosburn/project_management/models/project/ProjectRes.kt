package com.ferosburn.project_management.models.project

import kotlinx.serialization.Serializable

@Serializable
data class ProjectRes(
    val id: String,
    val name: String,
    val description: String,
    val isActive: Boolean,
)
