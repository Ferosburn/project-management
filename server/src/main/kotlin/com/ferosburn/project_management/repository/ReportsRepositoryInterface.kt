@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.PageableResult
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.report.ReportUpdateReq
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ReportsRepositoryInterface {
    fun getAll(filter: ReportFilter): PageableResult<ReportRes>
    fun getAllByUserId(userId: Uuid, filter: ReportFilter): List<ReportRes>
    fun getById(id: Uuid): ReportRes?
    fun create(userId: Uuid, data: ReportCreateReq): ReportRes
    fun update(userId: String, id: Uuid, data: ReportUpdateReq): ReportRes?
    fun delete(userId: String, id: Uuid): ReportRes?
}