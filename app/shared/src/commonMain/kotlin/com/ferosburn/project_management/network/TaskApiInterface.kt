package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes
import com.ferosburn.project_management.models.task.TaskUpdateReq

interface TaskApiInterface {
    suspend fun getAllTasks(
        projectId: String, filter: TaskFilter = TaskFilter()
    ): NetworkResult<ApiResponse<List<TaskRes>>>

    suspend fun createTask(projectId: String, data: TaskCreateReq): NetworkResult<ApiResponse<TaskRes>>
    suspend fun updateTask(projectId: String, taskId: String, data: TaskUpdateReq): NetworkResult<ApiResponse<TaskRes>>
    suspend fun deleteTask(projectId: String, taskId: String): NetworkResult<ApiResponse<TaskRes>>
}