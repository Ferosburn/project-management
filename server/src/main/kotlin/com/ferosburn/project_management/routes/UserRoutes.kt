@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.*
import com.ferosburn.project_management.repository.UserRepository
import com.ferosburn.project_management.utils.getUuidParameter
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi


fun Route.userRoutes(userRepository: UserRepository) {
    route("/users") {
        get {
            val filter = UserFilter(
                limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10,
                page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1,
                role = call.request.queryParameters["role"]?.let { UserRole.valueOf(it) },
                division = call.request.queryParameters["division"]?.let { UserDivision.valueOf(it) },
                isActive = call.request.queryParameters["isActive"]?.toBooleanStrictOrNull(),
            )
            val users = userRepository.getAll(filter)
            call.respond(ApiResponse.success(users))
        }
        post {
            val req = call.receive<UserCreateReq>()
            val errors = req.validate()
            if (errors.isNotEmpty()) {
                call.respond(ApiResponse.badRequest(errors))
                return@post
            }
            val newUser = userRepository.create(req)
            call.respond(ApiResponse.created(newUser))
        }
        route("/{id}") {
            get {
                val uuid = call.getUuidParameter("id") ?: return@get
                val user = userRepository.getById(uuid) ?: return@get call.respond(
                    ApiResponse.notFound()
                )
                call.respond(ApiResponse.success(user))
            }
            put {
                val uuid = call.getUuidParameter("id") ?: return@put
                val req = call.receive<UserUpdateReq>()
                val updatedUser = userRepository.update(uuid, req) ?: return@put call.respond(
                    ApiResponse.notFound()
                )
                call.respond(ApiResponse.success(updatedUser))
            }
            delete {
                val uuid = call.getUuidParameter("id") ?: return@delete
                val deletedUser = userRepository.delete(uuid) ?: return@delete call.respond(
                    ApiResponse.notFound()
                )
                call.respond(ApiResponse.success(deletedUser))
            }
        }
    }
}