package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.user.UserRes
import io.ktor.client.call.*
import io.ktor.client.request.*

object ProfileApi : ProfileApiInterface {
    private val _client = KtorClient.client

    override suspend fun getUserProfile(): NetworkResult<ApiResponse<UserRes>> = safeApiCall(true) { token ->
        _client.get("/profile") {
            bearerAuth(token)
        }.body()
    }

    override suspend fun getContributedProjects(): NetworkResult<ApiResponse<List<ProjectRes>>> =
        safeApiCall(true) { token ->
            _client.get("/profile/projects") {
                bearerAuth(token)
            }.body()
        }

    override suspend fun getUserReports(): NetworkResult<ApiResponse<List<ReportRes>>> = safeApiCall(true) { token ->
        _client.get("/profile/reports") {
            bearerAuth(token)
        }.body()
    }
}