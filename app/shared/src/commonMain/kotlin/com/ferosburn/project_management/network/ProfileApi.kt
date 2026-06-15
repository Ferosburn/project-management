package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.UserRes
import io.ktor.client.call.*
import io.ktor.client.request.*

object ProfileApi : ProfileApiInterface {
    private val _client = KtorClient.client

    override suspend fun getUserProfile(): NetworkResult<ApiResponse<UserRes>> =
        safeApiCall(true) { token ->
            _client.get("/profile") {
                bearerAuth(token)
            }.body()
        }
}