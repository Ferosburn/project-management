@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.utils

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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

suspend fun ApplicationCall.checkRole(vararg allowedRoles: UserRole): Boolean {
    val principal = principal<JWTPrincipal>()!!
    val role = runCatching {
        principal.payload.getClaim("role").asString().let { UserRole.valueOf(it) }
    }.getOrNull() ?: run {
        respond(HttpStatusCode.Forbidden, ApiResponse.forbidden())
        return false
    }
    if (role !in allowedRoles) {
        respond(HttpStatusCode.Forbidden, ApiResponse.forbidden())
        return false
    }
    return true
}

suspend fun ApplicationCall.checkUserEligibility(uuid: Uuid): Boolean {
    val principal = principal<JWTPrincipal>()!!
    val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
    if (userId == uuid) {
        respond(
            HttpStatusCode.Forbidden,
            ApiResponse.forbidden(listOf("Not allowed to alter yourself")),
        )
        return false
    }
    return true
}