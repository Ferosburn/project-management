package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes

interface TaskApiInterface {
    suspend fun getAllTasks(
        projectId: String, filter: TaskFilter = TaskFilter()
    ): NetworkResult<ApiResponse<List<TaskRes>>>

    suspend fun createTask(projectId: String, data: TaskCreateReq): NetworkResult<ApiResponse<TaskRes>>
}