package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.utils.setQueryParams
import io.ktor.client.call.*
import io.ktor.client.request.*

object UserApi : UserApiInterface {
    private val client = KtorClient.client

    override suspend fun getAllUsers(params: UserFilter): NetworkResult<ApiResponse<List<UserRes>>> =
        safeApiCall(true) { token ->
            client.get("/users") {
                bearerAuth(token)
                setQueryParams(params)
            }.body()
        }

    /*override suspend fun getUserDetail(userId: String): NetworkResult<ApiResponse<UserRes>> = safeApiCall(true) {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(data: UserCreateReq): NetworkResult<ApiResponse<UserRes>> = safeApiCall(true) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(
        userId: String,
        data: UserUpdateReq,
    ): NetworkResult<ApiResponse<UserRes>> = safeApiCall(true) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(userId: String): NetworkResult<ApiResponse<UserRes>> = safeApiCall(true) {
        TODO("Not yet implemented")
    }*/

}