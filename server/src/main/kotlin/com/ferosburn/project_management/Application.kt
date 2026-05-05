package com.ferosburn.project_management

import com.ferosburn.project_management.plugins.configureRouting
import com.ferosburn.project_management.plugins.configureSecurity
import com.ferosburn.project_management.plugins.configureSerialization
import com.ferosburn.project_management.plugins.configureStatusPages
import com.ferosburn.project_management.plugins.initDatabase
import com.ferosburn.project_management.repository.AuthRepository
import com.ferosburn.project_management.repository.UserRepository
import com.ferosburn.project_management.utils.JWTUtils
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    initDatabase()
    JWTUtils.init()
    configureSecurity()
    configureStatusPages()
    configureSerialization()
    val userRepository = UserRepository()
    val authRepository = AuthRepository()
    configureRouting(userRepository, authRepository)
}