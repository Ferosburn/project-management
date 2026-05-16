package com.ferosburn.project_management.plugins

import com.ferosburn.project_management.repository.AuthRepository
import com.ferosburn.project_management.repository.ProjectRepository
import com.ferosburn.project_management.repository.UserRepository
import com.ferosburn.project_management.routes.authRoutes
import com.ferosburn.project_management.routes.projectRoutes
import com.ferosburn.project_management.routes.userRoutes
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(
    userRepository: UserRepository,
    authRepository: AuthRepository,
    projectRepository: ProjectRepository,
) {
    routing {
        get("/") {
            call.respondText("Welcome to the project", ContentType.Text.Html)
        }
        userRoutes(userRepository)
        authRoutes(authRepository)
        projectRoutes(projectRepository)
    }
}