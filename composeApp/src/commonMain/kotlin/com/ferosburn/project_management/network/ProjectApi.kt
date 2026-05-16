package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq

object ProjectApi : ProjectApiInterface {
    override suspend fun getAllProjects(filter: ProjectFilter): NetworkResult<ApiResponse<List<ProjectRes>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectDetail(projectId: String): NetworkResult<ApiResponse<ProjectRes>> {
        TODO("Not yet implemented")
    }

    override suspend fun createProject(data: ProjectCreateReq): NetworkResult<ApiResponse<ProjectRes>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(
        projectId: String, data: ProjectUpdateReq
    ): NetworkResult<ApiResponse<ProjectRes>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProject(projectId: String): NetworkResult<ApiResponse<ProjectRes>> {
        TODO("Not yet implemented")
    }
}