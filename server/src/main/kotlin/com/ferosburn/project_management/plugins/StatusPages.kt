package com.ferosburn.project_management.plugins

import com.ferosburn.project_management.models.ApiResponse
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ExposedSQLException> { call, cause ->
            val pgException = cause.cause as? PSQLException
            when {
                pgException?.message?.contains("duplicate key") == true -> {
                    val field = extractDuplicateField(cause.message)
                    call.respond(ApiResponse.conflict(listOf("$field already exists")))
                }

                else -> call.respond(ApiResponse.internalError(listOf("Database error")))
            }
        }

        exception<Throwable> { call, _ ->
            call.application.log.error("Unhandled exception")
            call.respond(ApiResponse.internalError())
        }
    }
}

private fun extractDuplicateField(message: String?): String {
    return message
        ?.substringAfter("Key (")
        ?.substringBefore(")")
        ?: "field"
}