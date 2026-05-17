package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq
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

object ProjectApi : ProjectApiInterface {
    private val client = KtorClient.client

    override suspend fun getAllProjects(filter: ProjectFilter): NetworkResult<ApiResponse<List<ProjectRes>>> =
        safeApiCall(true) { token ->
            client.get("/projects") {
                bearerAuth(token)
                setQueryParams(filter)
            }.body()
        }

    override suspend fun getProjectDetail(projectId: String): NetworkResult<ApiResponse<ProjectRes>> =
        safeApiCall(true) { token ->
            client.get("/projects/$projectId") {
                bearerAuth(token)
            }.body()
        }

    override suspend fun createProject(data: ProjectCreateReq): NetworkResult<ApiResponse<ProjectRes>> =
        safeApiCall(true) { token ->
            client.post("/projects") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
        }

    override suspend fun updateProject(
        projectId: String, data: ProjectUpdateReq
    ): NetworkResult<ApiResponse<ProjectRes>> = safeApiCall(true) { token ->
        client.patch("/projects/$projectId") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    override suspend fun deleteProject(projectId: String): NetworkResult<ApiResponse<ProjectRes>> =
        safeApiCall(true) { token ->
            client.delete("/projects/$projectId") {
                bearerAuth(token)
            }.body()
        }
}