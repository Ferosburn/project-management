package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.report.ReportUpdateReq
import com.ferosburn.project_management.utils.setQueryParams
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object ReportApi : ReportApiInterface {
    private val client = KtorClient.client

    override suspend fun getAllReports(filter: ReportFilter): NetworkResult<ApiResponse<List<ReportRes>>> =
        safeApiCall(true) { token ->
            client.get("/reports") {
                bearerAuth(token)
                setQueryParams(filter)
            }.body()
        }

    override suspend fun getAllReportsOfMe(filter: ReportFilter): NetworkResult<ApiResponse<List<ReportRes>>> =
        safeApiCall(true) { token ->
            client.get("/reports/me") {
                bearerAuth(token)
            }.body()
        }

    override suspend fun getReportDetail(id: String): NetworkResult<ApiResponse<ReportRes>> =
        safeApiCall(true) { token ->
            client.get("/reports/$id") {
                bearerAuth(token)
            }.body()
        }

    override suspend fun createReport(data: ReportCreateReq): NetworkResult<ApiResponse<ReportRes>> =
        safeApiCall(true) { token ->
            client.post("/reports") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
        }

    override suspend fun updateReport(id: String, data: ReportUpdateReq): NetworkResult<ApiResponse<ReportRes>> =
        safeApiCall(true) { token ->
            client.patch("/reports/$id") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
        }

    override suspend fun deleteReport(id: String): NetworkResult<ApiResponse<ReportRes>> = safeApiCall(true) { token ->
        client.delete("/reports/$id") {
            bearerAuth(token)
        }.body()
    }
}