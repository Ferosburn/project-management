@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.repository.ProjectRepository
import com.ferosburn.project_management.repository.ReportRepository
import com.ferosburn.project_management.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.profileRoutes(
    userRepository: UserRepository,
    projectRepository: ProjectRepository,
    reportRepository: ReportRepository,
) {
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
            get("/projects") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                val projects = transaction { projectRepository.getAllContributedProjects(userId) }
                call.respond(HttpStatusCode.OK, ApiResponse.success(projects))
            }
            get("/reports") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                val reports = reportRepository.getAllByUserId(userId)
                call.respond(HttpStatusCode.OK, ApiResponse.success(reports))
            }
        }
    }
}