package com.ferosburn.project_management.models.report

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class ReportCreateReq(
    val projectId: String = "",
    val detail: String = "",
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (projectId.isBlank()) add("projectId is required")
        if (detail.isBlank()) add("detail is required")
    }
}