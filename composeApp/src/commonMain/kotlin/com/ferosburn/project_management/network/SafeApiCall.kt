package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.navigation.NavChannel
import com.ferosburn.project_management.storage.LocalStorage
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.http.HttpStatusCode

suspend fun <T> safeApiCall(
    requireAuth: Boolean = false,
    apiCall: suspend (token: String) -> T,
): NetworkResult<T> {
    return try {
        val token = LocalStorage.readAccessToken() ?: ""
        if (requireAuth && token.isBlank()) return NetworkResult.Error("Access token not set")
        NetworkResult.Success(apiCall(token))
    } catch (e: ClientRequestException) {
        if (e.response.status == HttpStatusCode.Unauthorized && requireAuth) {
            val refreshToken = LocalStorage.readRefreshToken() ?: return NetworkResult.Error("Refresh token not set")
            return when (val refresh = AuthApi.refreshToken(AuthRefreshTokenReq(refreshToken))) {
                is NetworkResult.Success -> {
                    refresh.data.data?.let {
                        LocalStorage.saveAccessToken(it.accessToken)
                        LocalStorage.saveRefreshToken(it.refreshToken)
                    }
                    val accessToken =
                        LocalStorage.readRefreshToken() ?: return NetworkResult.Error("Access token not set")
                    safeApiCall(true) { apiCall(accessToken) }
                }

                is NetworkResult.Error -> {
                    LocalStorage.deleteTokens()
                    NavChannel.navigateToLogin()
                    NetworkResult.Error(refresh.message)
                }
            }
        }
        try {
            val errorResponse = e.response.body<ApiResponse<Nothing>>()
            NetworkResult.Error(
                errorResponse.errors?.let { errors ->
                    if (errors.size == 1) errors.first()
                    else errors.joinToString("\n") { "- $it" }
                } ?: errorResponse.message,
            )
        } catch (serializationException: Exception) {
            NetworkResult.Error("Server error: ${e.response.status.value}")
        }
    } catch (e: Exception) {
        NetworkResult.Error("Network error: Please check your connection.")
    }
}