package com.ferosburn.project_management.models.task

import kotlinx.serialization.Serializable

@Serializable
enum class TaskStatus {
    BACKLOG,
    TODO,
    IN_PROGRESS,
    TESTING,
    DONE,
    BLOCKED,
    REJECTED,
    BUG_FOUND,
}