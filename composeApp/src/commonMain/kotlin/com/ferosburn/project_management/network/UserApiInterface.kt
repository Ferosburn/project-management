package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.UserCreateReq
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes

interface UserApiInterface {
    suspend fun getAllUsers(params: UserFilter = UserFilter()): NetworkResult<ApiResponse<List<UserRes>>>
    suspend fun getUserDetail(userId: String): NetworkResult<ApiResponse<UserRes>>
    suspend fun createUser(data: UserCreateReq): NetworkResult<ApiResponse<UserRes>>
    /*suspend fun updateUser(userId: String, data: UserUpdateReq): NetworkResult<ApiResponse<UserRes>>
    suspend fun deleteUser(userId: String): NetworkResult<ApiResponse<UserRes>>*/
}