package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.user.UserRes

interface ProfileApiInterface {
    suspend fun getUserProfile() : NetworkResult<ApiResponse<UserRes>>
    suspend fun getContributedProjects() : NetworkResult<ApiResponse<List<ProjectRes>>>
}