package com.ferosburn.project_management.models.project

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class ProjectCreateReq(
    val name: String = "",
    val description: String = "",
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (name.isBlank()) add("name is required")
        if (description.isBlank()) add("description is required")
    }
}
