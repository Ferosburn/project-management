@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.utils

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.Pagination
import com.ferosburn.project_management.models.user.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlin.math.ceil
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

suspend inline fun <reified T> ApplicationCall.respondPaginated(
    data: List<T>,
    totalData: Long,
    page: Int,
    limit: Int
) {
    val totalPage = if (totalData <= 0) 1 else ceil(totalData.toDouble() / limit).toInt()
    val pagination = Pagination(
        totalData = totalData.toInt(),
        page = page,
        perPage = limit,
        totalPage = totalPage
    )

    this.respond(HttpStatusCode.OK, ApiResponse.success(data, pagination))
}