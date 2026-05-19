package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.report.ReportUpdateReq

interface ReportApiInterface {
    suspend fun getAllReports(filter: ReportFilter = ReportFilter()): NetworkResult<ApiResponse<List<ReportRes>>>
    suspend fun getAllReportsOfMe(filter: ReportFilter = ReportFilter()): NetworkResult<ApiResponse<List<ReportRes>>>
    suspend fun getReportDetail(id: String): NetworkResult<ApiResponse<ReportRes>>
    suspend fun createReport(data: ReportCreateReq): NetworkResult<ApiResponse<ReportRes>>
    suspend fun updateReport(id: String, data: ReportUpdateReq): NetworkResult<ApiResponse<ReportRes>>
    suspend fun deleteReport(id: String): NetworkResult<ApiResponse<ReportRes>>
}