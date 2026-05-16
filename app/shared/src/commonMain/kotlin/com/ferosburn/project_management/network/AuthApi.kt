package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.auth.AuthChangePasswordReq
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.models.auth.AuthTokenRes
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object AuthApi : AuthApiInterface {
    private val client = KtorClient.client

    override suspend fun login(data: AuthLoginReq): NetworkResult<ApiResponse<AuthTokenRes>> = safeApiCall {
        client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    override suspend fun refreshToken(data: AuthRefreshTokenReq): NetworkResult<ApiResponse<AuthTokenRes>> =
        safeApiCall {
            client.post("/auth/refresh-token") {
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
        }

    override suspend fun logout(): NetworkResult<ApiResponse<Nothing>> = safeApiCall(true) {
        client.post("/auth/logout") {
            bearerAuth(it)
        }.body()
    }

    override suspend fun changePassword(data: AuthChangePasswordReq): NetworkResult<ApiResponse<Nothing>> =
        safeApiCall(true) {
            client.post("/auth/change-password") {
                bearerAuth(it)
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
        }

    override suspend fun checkToken(): NetworkResult<ApiResponse<Nothing>> = safeApiCall(true) {
        client.get("/auth/check-token") {
            bearerAuth(it)
        }.body()
    }
}