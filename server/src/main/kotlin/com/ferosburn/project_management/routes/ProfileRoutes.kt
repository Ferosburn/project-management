@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.profileRoutes(userRepository: UserRepository) {
    authenticate("jwt") {
        route("/profile") {
            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                val user = userRepository.getById(userId) ?: return@get call.respond(
                    HttpStatusCode.NotFound, ApiResponse.notFound()
                )
                call.respond(HttpStatusCode.OK, ApiResponse.success(user))
            }
        }
    }
}