package com.ferosburn.project_management.plugins

import com.ferosburn.project_management.repository.UserRepository
import com.ferosburn.project_management.routes.userRoutes
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(userRepository: UserRepository) {
    routing {
        get ("/") {
            call.respondText("Welcome to the project", ContentType.Text.Html)
        }
        userRoutes(userRepository)
    }
}