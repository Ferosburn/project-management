@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectUpdateReq
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.repository.ProjectRepository
import com.ferosburn.project_management.utils.checkRole
import com.ferosburn.project_management.utils.getUuidParameter
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi

fun Route.projectRoutes(projectRepository: ProjectRepository) {
    authenticate("jwt") {
        route("/projects") {
            get {
                val filter = ProjectFilter(
                    limit = call.request.queryParameters["limit"]?.toInt() ?: 10,
                    page = call.request.queryParameters["page"]?.toInt() ?: 1,
                    isActive = call.request.queryParameters["isActive"]?.toBooleanStrictOrNull(),
                )
                val projects = projectRepository.getAll(filter)
                call.respond(HttpStatusCode.OK, ApiResponse.success(projects))
            }
            post {
                if (!call.checkRole(UserRole.MANAGER)) return@post
                val req = call.receive<ProjectCreateReq>()
                val errors = req.validate()
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
                    return@post
                }
                val newProject = projectRepository.create(req)
                call.respond(HttpStatusCode.Created, ApiResponse.created(newProject))
            }
            route("/{id}") {
                get {
                    val uuid = call.getUuidParameter("id") ?: return@get
                    val project = projectRepository.getById(uuid) ?: return@get call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(project))
                }
                patch {
                    if (!call.checkRole(UserRole.MANAGER)) return@patch
                    val uuid = call.getUuidParameter("id") ?: return@patch
                    val req = call.receive<ProjectUpdateReq>()
                    val updatedProject = projectRepository.update(uuid, req) ?: return@patch call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(updatedProject))
                }
                delete {
                    if (!call.checkRole(UserRole.MANAGER)) return@delete
                    val uuid = call.getUuidParameter("id") ?: return@delete
                    val deletedProject = projectRepository.delete(uuid) ?: return@delete call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(deletedProject))
                }
            }
        }
    }
}