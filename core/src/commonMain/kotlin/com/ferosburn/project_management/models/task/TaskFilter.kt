package com.ferosburn.project_management.models.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskFilter(
    val limit: Int = 10,
    val page: Int = 1,
)