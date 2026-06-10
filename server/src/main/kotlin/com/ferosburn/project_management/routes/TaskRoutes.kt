@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskUpdateReq
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.repository.TaskRepository
import com.ferosburn.project_management.utils.checkRole
import com.ferosburn.project_management.utils.getUuidParameter
import com.ferosburn.project_management.utils.respondPaginated
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi

fun Route.taskRoutes(taskRepository: TaskRepository) {
    authenticate("jwt") {
        route("/tasks/{projectId}") {
            get {
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
                val page = call.request.queryParameters["page"]?.toInt() ?: 1
                val filter = TaskFilter(limit = limit, page = page)
                val projectId = call.getUuidParameter("projectId") ?: return@get
                val (tasks, totalData) = transaction { taskRepository.getAllByProjectId(projectId, filter) }
                call.respondPaginated(tasks, totalData, page, limit)
            }
            post {
                if (!call.checkRole(UserRole.MANAGER)) return@post
                val projectId = call.getUuidParameter("projectId") ?: return@post
                val req = call.receive<TaskCreateReq>()
                val errors = req.validate()
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
                    return@post
                }
                val newTask = taskRepository.create(projectId, req)
                call.respond(HttpStatusCode.Created, ApiResponse.created(newTask))
            }
            route("/{id}") {
                get {
                    val id = call.getUuidParameter("id") ?: return@get
                    val task = taskRepository.getById(id) ?: return@get call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(task))
                }
                patch {
                    if (!call.checkRole(UserRole.MANAGER)) return@patch
                    val id = call.getUuidParameter("id") ?: return@patch
                    val req = call.receive<TaskUpdateReq>()
                    val errors = req.validate()
                    if (errors.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
                        return@patch
                    }
                    val updatedTask = taskRepository.update(id, req) ?: return@patch call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(updatedTask))
                }
                delete {
                    if (!call.checkRole(UserRole.MANAGER)) return@delete
                    val id = call.getUuidParameter("id") ?: return@delete
                    val deletedTask = taskRepository.delete(id) ?: return@delete call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(deletedTask))
                }
            }
        }
    }
}