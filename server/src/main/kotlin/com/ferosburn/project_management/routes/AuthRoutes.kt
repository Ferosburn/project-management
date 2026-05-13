@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.auth.AuthChangePasswordReq
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.repository.AuthRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.authRoutes(authRepository: AuthRepository) {
    route("/auth") {
        post("/login") {
            val req = call.receive<AuthLoginReq>()
            val errors = req.validate()
            if (errors.isNotEmpty()) return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
            val tokens = authRepository.login(req)
            call.respond(HttpStatusCode.OK, ApiResponse.success(tokens))
        }

        post("/refresh-token") {
            val req = call.receive<AuthRefreshTokenReq>()
            val errors = req.validate()
            if (errors.isNotEmpty()) return@post call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
            val token = authRepository.refreshToken(req) ?: return@post call.respond(
                HttpStatusCode.Unauthorized, ApiResponse.unauthorized()
            )
            call.respond(HttpStatusCode.OK, ApiResponse.success(token))
        }

        authenticate("jwt") {
            get("/check-token") {
                call.respond(HttpStatusCode.OK, ApiResponse.success(null))
            }

            post("/logout") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                authRepository.logout(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(null))
            }

            post("/change-password") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                val req = call.receive<AuthChangePasswordReq>()
                val errors = req.validate()
                if (errors.isNotEmpty()) return@post call.respond(
                    HttpStatusCode.BadRequest, ApiResponse.badRequest(errors)
                )
                val isSuccess = authRepository.changePassword(userId, req)
                if (!isSuccess) return@post call.respond(
                    HttpStatusCode.BadRequest, ApiResponse.badRequest(listOf("Failed to change password"))
                )
                call.respond(HttpStatusCode.OK, ApiResponse.success(null))
            }
        }
    }
}