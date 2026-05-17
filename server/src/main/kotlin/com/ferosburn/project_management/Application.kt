package com.ferosburn.project_management

import com.ferosburn.project_management.plugins.*
import com.ferosburn.project_management.repository.AuthRepository
import com.ferosburn.project_management.repository.ProjectRepository
import com.ferosburn.project_management.repository.TaskRepository
import com.ferosburn.project_management.repository.UserRepository
import com.ferosburn.project_management.utils.JWTUtils
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    initDatabase()
    JWTUtils.init()
    configureCORS()
    configureSecurity()
    configureStatusPages()
    configureSerialization()
    val userRepository = UserRepository()
    val authRepository = AuthRepository()
    val projectRepository = ProjectRepository()
    val taskRepository = TaskRepository()
    configureRouting(userRepository, authRepository, projectRepository, taskRepository)
}