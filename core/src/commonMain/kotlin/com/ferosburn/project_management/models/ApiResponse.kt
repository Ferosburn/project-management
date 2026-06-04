package com.ferosburn.project_management.models

import io.ktor.http.*
import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null,
    val pagination: Pagination? = null,
) {
    companion object {
        fun <T> success(data: T, pagination: Pagination? = null) = ApiResponse(
            HttpStatusCode.OK.value,
            HttpStatusCode.OK.description,
            data,
            pagination = pagination,
        )

        fun <T> created(data: T) = ApiResponse(
            HttpStatusCode.Created.value,
            HttpStatusCode.Created.description,
            data,
        )

        fun notFound(errors: List<String>? = null) = ApiResponse<Nothing>(
            HttpStatusCode.NotFound.value,
            HttpStatusCode.NotFound.description,
            errors = errors,
        )

        fun badRequest(errors: List<String>? = null) = ApiResponse<Nothing>(
            HttpStatusCode.BadRequest.value,
            HttpStatusCode.BadRequest.description,
            errors = errors,
        )

        fun conflict(errors: List<String>? = null) = ApiResponse<Nothing>(
            HttpStatusCode.Conflict.value,
            HttpStatusCode.Conflict.description,
            errors = errors,
        )

        fun internalError(errors: List<String>? = null) = ApiResponse<Nothing>(
            HttpStatusCode.InternalServerError.value,
            HttpStatusCode.InternalServerError.description,
            errors = errors,
        )

        fun unauthorized(errors: List<String>? = null) = ApiResponse<Nothing>(
            HttpStatusCode.Unauthorized.value,
            HttpStatusCode.Unauthorized.description,
            errors = errors,
        )

        fun forbidden(errors: List<String>? = null) = ApiResponse<Nothing>(
            HttpStatusCode.Forbidden.value,
            HttpStatusCode.Forbidden.description,
            errors = errors,
        )
    }
}

@Serializable
data class Pagination(
    val totalData: Int = 0,
    val page: Int = 1,
    val perPage: Int = 10,
    val totalPage: Int = 1,
)

data class PageableResult<T>(val items: List<T>, val totalCount: Long)