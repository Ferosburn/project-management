package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.models.auth.AuthTokenRes
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

object AuthApi {
    private val client = KtorClient.client

    suspend fun login(data: AuthLoginReq): NetworkResult<ApiResponse<AuthTokenRes>> = safeApiCall {
        client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }
}