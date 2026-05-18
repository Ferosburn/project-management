package com.ferosburn.project_management.models.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskRes(
    val id: String,
    val name: String,
    val description: String,
    val status: TaskStatus,
    val parentId: String? = null,
    val rootParentId: String? = null,
    val tasks: List<TaskRes> = emptyList(),
)