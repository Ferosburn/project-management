package com.ferosburn.project_management.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ferosburn.project_management.models.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond

fun Application.configureSecurity() {
    val jwtSecret = System.getenv("JWT_SECRET") ?: error("JWT_SECRET environment variable not set")
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: error("JWT_ISSUER environment variable not set")
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: error("JWT_AUDIENCE environment variable not set")
    val jwtRealm = System.getenv("JWT_REALM") ?: error("JWT_REALM environment variable not set")

    install(Authentication) {
        jwt("jwt") {
            realm = jwtRealm
            verifier(JWT.require(Algorithm.HMAC256(jwtSecret)).withAudience(jwtAudience).withIssuer(jwtIssuer).build())
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null && credential.payload.audience.contains(
                        jwtAudience
                    )
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiResponse.unauthorized(listOf("Token is invalid or expired")),
                )
            }
        }
    }
}