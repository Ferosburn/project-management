@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.routes

import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportUpdateReq
import com.ferosburn.project_management.repository.ReportRepository
import com.ferosburn.project_management.utils.getUuidParameter
import com.ferosburn.project_management.utils.respondPaginated
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.reportRoutes(reportRepository: ReportRepository) {
    authenticate("jwt") {
        route("/reports") {
            get {
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
                val page = call.request.queryParameters["page"]?.toInt() ?: 1
                val filter = ReportFilter(
                    limit = limit, page = page, projectId = call.request.queryParameters["projectId"]
                )
                val (reports, totalData) = transaction { reportRepository.getAll(filter) }
                call.respondPaginated(reports, totalData, page, limit)
            }
            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                val filter = ReportFilter(
                    limit = call.request.queryParameters["limit"]?.toInt() ?: 10,
                    page = call.request.queryParameters["page"]?.toInt() ?: 1,
                    projectId = call.request.queryParameters["projectId"]
                )
                val reports = reportRepository.getAllByUserId(userId, filter)
                call.respond(HttpStatusCode.OK, ApiResponse.success(reports))
            }
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = Uuid.parse(principal.payload.getClaim("userId").asString())
                val req = call.receive<ReportCreateReq>()
                val errors = req.validate()
                if (errors.isNotEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
                    return@post
                }
                val newReport = reportRepository.create(userId, req)
                call.respond(HttpStatusCode.Created, ApiResponse.created(newReport))
            }
            route("/{id}") {
                get {
                    val id = call.getUuidParameter("id") ?: return@get
                    val report = reportRepository.getById(id) ?: return@get call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(report))
                }
                patch {
                    val id = call.getUuidParameter("id") ?: return@patch
                    val req = call.receive<ReportUpdateReq>()
                    val errors = req.validate()
                    if (errors.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.badRequest(errors))
                        return@patch
                    }
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("userId").asString()
                    val updatedReport = reportRepository.update(userId, id, req) ?: return@patch call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(updatedReport))
                }
                delete {
                    val id = call.getUuidParameter("id") ?: return@delete
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("userId").asString()
                    val deletedReport = reportRepository.delete(userId, id) ?: return@delete call.respond(
                        HttpStatusCode.NotFound, ApiResponse.notFound()
                    )
                    call.respond(HttpStatusCode.OK, ApiResponse.success(deletedReport))
                }
            }
        }
    }
}