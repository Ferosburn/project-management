package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.auth.AuthChangePasswordReq
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.models.auth.AuthTokenRes

interface AuthApiInterface {
    suspend fun login(data: AuthLoginReq): NetworkResult<ApiResponse<AuthTokenRes>>
    suspend fun refreshToken(data: AuthRefreshTokenReq): NetworkResult<ApiResponse<AuthTokenRes>>
    suspend fun logout(): NetworkResult<ApiResponse<Nothing>>
    suspend fun changePassword(data: AuthChangePasswordReq): NetworkResult<ApiResponse<Nothing>>
    suspend fun checkToken(): NetworkResult<ApiResponse<Nothing>>
}