package com.ferosburn.project_management.models.report

import com.ferosburn.project_management.models.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class ReportUpdateReq(
    val detail: String? = null,
) : Validatable {
    override fun validate(): List<String> = buildList {
        if (detail != null && detail.isBlank()) add("detail is required")
    }
}