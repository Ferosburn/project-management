package com.ferosburn.project_management.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.userRoutes() {
    route("/users") {
        get { }
        post { }
        route("/{id}") {
            get { }
            put { }
            delete { }
        }
    }
}