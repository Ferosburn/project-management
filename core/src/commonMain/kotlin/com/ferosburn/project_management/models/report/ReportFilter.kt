package com.ferosburn.project_management.models.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportFilter(
    val limit: Int = 10,
    val page: Int = 1,
    val projectId: String? = null,
)