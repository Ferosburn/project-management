package com.ferosburn.project_management.models.report

import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.user.UserRes
import kotlinx.serialization.Serializable

@Serializable
data class ReportRes(
    val id: String,
    val detail: String,
    val createdAt: Long,
    val user: UserRes,
    val project: ProjectRes,
)