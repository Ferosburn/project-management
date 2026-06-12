package com.ferosburn.project_management.models.task

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class TaskUpdateReq(
    val name: String? = null,
    val description: String? = null,
    val status: String? = null,
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (name != null && name.isBlank()) add("name is required")
        if (description != null && description.isBlank()) add("description is required")
    }
}