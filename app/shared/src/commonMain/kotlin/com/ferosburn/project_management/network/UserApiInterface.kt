package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.UserCreateReq
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.models.user.UserUpdateReq

interface UserApiInterface {
    suspend fun getAllUsers(filter: UserFilter = UserFilter()): NetworkResult<ApiResponse<List<UserRes>>>
    suspend fun getUserDetail(userId: String): NetworkResult<ApiResponse<UserRes>>
    suspend fun createUser(data: UserCreateReq): NetworkResult<ApiResponse<UserRes>>
    suspend fun updateUser(userId: String, data: UserUpdateReq): NetworkResult<ApiResponse<UserRes>>
    suspend fun deleteUser(userId: String): NetworkResult<ApiResponse<UserRes>>
}