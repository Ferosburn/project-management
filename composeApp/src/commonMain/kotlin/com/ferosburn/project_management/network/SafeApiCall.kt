package com.ferosburn.project_management.network

import com.ferosburn.project_management.models.ApiResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.*

suspend inline fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: ClientRequestException) {
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