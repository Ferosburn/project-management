package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq

interface ProjectApiInterface {
    suspend fun getAllProjects(filter: ProjectFilter = ProjectFilter()): NetworkResult<ApiResponse<List<ProjectRes>>>
    suspend fun getProjectDetail(projectId: String): NetworkResult<ApiResponse<ProjectRes>>
    suspend fun createProject(data: ProjectCreateReq): NetworkResult<ApiResponse<ProjectRes>>
    suspend fun updateProject(projectId: String, data: ProjectUpdateReq): NetworkResult<ApiResponse<ProjectRes>>
    suspend fun deleteProject(projectId: String): NetworkResult<ApiResponse<ProjectRes>>
}