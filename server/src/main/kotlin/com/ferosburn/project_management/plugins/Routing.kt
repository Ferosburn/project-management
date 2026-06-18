package com.ferosburn.project_management.plugins

import com.ferosburn.project_management.repository.*
import com.ferosburn.project_management.routes.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userRepository: UserRepository,
    authRepository: AuthRepository,
    projectRepository: ProjectRepository,
    taskRepository: TaskRepository,
    reportRepository: ReportRepository,
) {
    routing {
        get("/") { call.respondText("Welcome! Please use the proper routes.", ContentType.Text.Html) }
        userRoutes(userRepository)
        authRoutes(authRepository)
        projectRoutes(projectRepository, userRepository)
        taskRoutes(taskRepository)
        reportRoutes(reportRepository)
        profileRoutes(userRepository, projectRepository, reportRepository)
    }
}