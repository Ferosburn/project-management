package com.ferosburn.project_management.models.task

import kotlinx.serialization.Serializable

@Serializable
enum class TaskStatus(val label: String) {
    BACKLOG(label = "Backlog"),
    TODO(label = "Todo"),
    IN_PROGRESS(label = "In Progress"),
    TESTING(label = "Testing"),
    DONE(label = "Done"),
    BLOCKED(label = "Blocked"),
    REJECTED(label = "Rejected"),
    BUG_FOUND(label = "Bug Found"),
}