package com.ferosburn.project_management.plugins

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.utils.BadRequestException
import com.ferosburn.project_management.utils.ForbiddenException
import com.ferosburn.project_management.utils.InternalServerException
import com.ferosburn.project_management.utils.UnauthorizedException
import com.ferosburn.project_management.utils.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ApiResponse.unauthorized(cause.message?.let { listOf(it) }))
        }
        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ApiResponse.forbidden(cause.message?.let { listOf(it) }))
        }
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(cause.message?.let { listOf(it) }))
        }
        exception<InternalServerException> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError, ApiResponse.internalError(cause.message?.let { listOf(it) })
            )
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiResponse.notFound(cause.message?.let { listOf(it) }))
        }
        exception<ExposedSQLException> { call, cause ->
            val pgException = cause.cause as? PSQLException
            when {
                pgException?.message?.contains("duplicate key") == true -> {
                    val field = extractDuplicateField(cause.message)
                    call.respond(HttpStatusCode.Conflict, ApiResponse.conflict(listOf("$field already exists")))
                }

                else -> call.respond(
                    HttpStatusCode.InternalServerError, ApiResponse.internalError(listOf("Database error"))
                )
            }
        }
        exception<Throwable> { call, _ ->
            call.application.log.error("Unhandled exception")
            call.respond(HttpStatusCode.InternalServerError, ApiResponse.internalError())
        }
    }
}

private fun extractDuplicateField(message: String?): String {
    return message?.substringAfter("Key (")?.substringBefore(")") ?: "field"
}