@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.utils

import com.ferosburn.project_management.models.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

suspend fun ApplicationCall.getUuidParameter(name: String): Uuid? {
    val id = parameters[name] ?: run {
        respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(listOf("$name is required")))
        return null
    }
    return try {
        Uuid.parse(id)
    } catch (_: IllegalArgumentException) {
        respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(listOf("$name is not valid")))
        null
    }
}