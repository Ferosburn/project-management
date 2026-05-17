@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.repository.TaskRepository
import com.ferosburn.project_management.utils.getUuidParameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlin.uuid.ExperimentalUuidApi

fun Route.taskRoutes(taskRepository: TaskRepository) {
    authenticate("jwt") {
        route("/tasks/{projectId}") {
            get {
                val filter = TaskFilter(
                    limit = call.request.queryParameters["limit"]?.toInt() ?: 10,
                    page = call.request.queryParameters["page"]?.toInt() ?: 1,
                )
                val projectId = call.getUuidParameter("projectId") ?: return@get
                val tasks = taskRepository.getAllByProjectId(projectId, filter)
                call.respond(HttpStatusCode.OK, ApiResponse.success(tasks))
            }
        }
    }
}