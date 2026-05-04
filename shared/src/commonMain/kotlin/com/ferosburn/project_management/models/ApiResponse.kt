package com.ferosburn.project_management.models

import io.ktor.http.*
import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null,
) {
    companion object {
        fun <T> success(data: T) = ApiResponse(
            HttpStatusCode.OK.value,
            HttpStatusCode.OK.description,
            data,
        )

        fun <T> created(data: T) = ApiResponse(
            HttpStatusCode.Created.value,
            HttpStatusCode.Created.description,
            data,
        )

        fun notFound() = ApiResponse<Unit>(
            HttpStatusCode.NotFound.value,
            HttpStatusCode.NotFound.description,
        )

        fun badRequest(errors: List<String>) = ApiResponse<Unit>(
            HttpStatusCode.BadRequest.value,
            HttpStatusCode.BadRequest.description,
            errors = errors,
        )

        fun conflict(errors: List<String>?) = ApiResponse<Unit>(
            HttpStatusCode.Conflict.value,
            HttpStatusCode.Conflict.description,
            errors = errors,
        )

        fun internalError(errors: List<String>? = null) = ApiResponse<Unit>(
            HttpStatusCode.InternalServerError.value,
            HttpStatusCode.InternalServerError.description,
            errors = errors,
        )
    }
}