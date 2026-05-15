@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.user.*
import com.ferosburn.project_management.repository.UserRepository
import com.ferosburn.project_management.utils.checkRole
import com.ferosburn.project_management.utils.getUuidParameter
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.ExperimentalUuidApi


fun Route.userRoutes(userRepository: UserRepository) {
    authenticate("jwt") {
        route("/users") {
            get {
                val filter = UserFilter(
                    limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10,
                    page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1,
                    role = runCatching { call.request.queryParameters["role"]?.let { UserRole.valueOf(it) } }.getOrNull(),
                    division = call.request.queryParameters["division"]?.let { UserDivision.valueOf(it) },
                    isActive = call.request.queryParameters["isActive"]?.toBooleanStrictOrNull(),
                )
                val users = userRepository.getAll(filter)
                call.respond(HttpStatusCode.OK, ApiResponse.success(users))
            }
            post {
                if (!call.checkRole(UserRole.SUPERADMIN)) return@post
                val req = call.receive<UserCreateReq>()
                val errors = req.validate()
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
                    return@post
                }
                val newUser = userRepository.create(req)
                call.respond(HttpStatusCode.Created, ApiResponse.created(newUser))
            }
            route("/{id}") {
                get {
                    val uuid = call.getUuidParameter("id") ?: return@get
                    val user = userRepository.getById(uuid) ?: return@get call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(user))
                }
                patch {
                    if (!call.checkRole(UserRole.SUPERADMIN)) return@patch
                    val uuid = call.getUuidParameter("id") ?: return@patch
                    val req = call.receive<UserUpdateReq>()
                    val updatedUser = userRepository.update(uuid, req) ?: return@patch call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(updatedUser))
                }
                delete {
                    if (!call.checkRole(UserRole.SUPERADMIN)) return@delete
                    val uuid = call.getUuidParameter("id") ?: return@delete
                    val deletedUser = userRepository.delete(uuid) ?: return@delete call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(deletedUser))
                }
            }
        }
    }
}