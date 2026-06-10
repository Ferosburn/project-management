package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.utils.setQueryParams
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

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

    override suspend fun getProjectContributors(projectId: String, filter: UserFilter): NetworkResult<ApiResponse<List<UserRes>>> =
        safeApiCall(true) { token ->
            client.get("/projects/$projectId/contributors") {
                bearerAuth(token)
                setQueryParams(filter)
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