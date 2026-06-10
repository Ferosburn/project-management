package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes
import com.ferosburn.project_management.models.task.TaskUpdateReq
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

object TaskApi : TaskApiInterface {
    private val client = KtorClient.client

    override suspend fun getAllTasks(
        projectId: String, filter: TaskFilter
    ): NetworkResult<ApiResponse<List<TaskRes>>> = safeApiCall(true) { token ->
        client.get("/tasks/$projectId") {
            bearerAuth(token)
            setQueryParams(filter)
        }.body()
    }

    override suspend fun getTask(projectId: String, taskId: String): NetworkResult<ApiResponse<TaskRes>> =
        safeApiCall(true) { token ->
            client.get("/tasks/$projectId/$taskId") {
                bearerAuth(token)
            }.body()
        }

    override suspend fun createTask(
        projectId: String, data: TaskCreateReq
    ): NetworkResult<ApiResponse<TaskRes>> = safeApiCall(true) { token ->
        client.post("/tasks/$projectId") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    override suspend fun updateTask(
        projectId: String, taskId: String, data: TaskUpdateReq
    ): NetworkResult<ApiResponse<TaskRes>> = safeApiCall { token ->
        client.patch("/tasks/$projectId/$taskId") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    override suspend fun deleteTask(projectId: String, taskId: String): NetworkResult<ApiResponse<TaskRes>> =
        safeApiCall { token ->
            client.delete("/tasks/$projectId/$taskId") {
                bearerAuth(token)
            }.body()
        }
}