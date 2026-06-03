@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.PageableResult
import com.ferosburn.project_management.models.ProjectsTable
import com.ferosburn.project_management.models.ReportsTable
import com.ferosburn.project_management.models.UsersTable
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.report.ReportUpdateReq
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.utils.ForbiddenException
import com.ferosburn.project_management.utils.InternalServerException
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ReportRepository : ReportsRepositoryInterface {
    private fun mapToReportRes(row: ResultRow): ReportRes {
        return ReportRes(
            id = row[ReportsTable.id].toString(),
            detail = row[ReportsTable.detail],
            createdAt = row[ReportsTable.createdAt],
            user = UserRes(
                id = row[UsersTable.id].toString(),
                name = row[UsersTable.name],
                username = row[UsersTable.username],
                role = row[UsersTable.role],
                division = row[UsersTable.division],
                isActive = row[UsersTable.isActive],
            ),
            project = ProjectRes(
                id = row[ProjectsTable.id].toString(),
                name = row[ProjectsTable.name],
                description = row[ProjectsTable.description],
                isActive = row[ProjectsTable.isActive],
            )
        )
    }

    override fun getAll(filter: ReportFilter): PageableResult<ReportRes> {
        val query = ReportsTable.innerJoin(ProjectsTable, { ReportsTable.projectId }, { ProjectsTable.id })
            .innerJoin(UsersTable, { ReportsTable.userId }, { UsersTable.id }).selectAll()
        val projectId = filter.projectId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        projectId?.let { query.andWhere { ReportsTable.projectId eq projectId } }
        val totalCount = query.count()
        val reports = query.apply {
            limit(filter.limit)
            offset(((filter.page - 1) * filter.limit).toLong())
            orderBy(ReportsTable.id to SortOrder.DESC)
        }.map { mapToReportRes(it) }
        return PageableResult(reports, totalCount)
    }

    override fun getAllByUserId(
        userId: Uuid, filter: ReportFilter
    ): List<ReportRes> = transaction {
        ReportsTable.innerJoin(ProjectsTable, { ReportsTable.projectId }, { ProjectsTable.id })
            .innerJoin(UsersTable, { ReportsTable.userId }, { UsersTable.id }).selectAll()
            .where { ReportsTable.userId eq userId }.apply {
                val projectId = filter.projectId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
                projectId?.let { andWhere { ReportsTable.id eq projectId } }
                limit(filter.limit)
                offset(((filter.page - 1) * filter.limit).toLong())
                orderBy(ReportsTable.id to SortOrder.DESC)
            }.map { mapToReportRes(it) }
    }

    override fun getById(id: Uuid): ReportRes? = transaction {
        ReportsTable.innerJoin(ProjectsTable, { ReportsTable.projectId }, { ProjectsTable.id })
            .innerJoin(UsersTable, { ReportsTable.userId }, { UsersTable.id }).selectAll()
            .where { ReportsTable.id eq id }.map { mapToReportRes(it) }.firstOrNull()
    }

    override fun create(
        userId: Uuid, data: ReportCreateReq
    ): ReportRes = transaction {
        val insertStatement = ReportsTable.insert { row ->
            row[id] = Uuid.generateV7()
            row[ReportsTable.userId] = userId
            row[projectId] = Uuid.parse(data.projectId)
            row[detail] = data.detail
            row[createdAt] = Clock.System.now().toEpochMilliseconds()
        }
        val reportId = insertStatement.resultedValues?.singleOrNull()?.let { it[ReportsTable.id] }
            ?: throw InternalServerException("Failed to create report")
        getById(reportId) ?: throw InternalServerException("Failed to create report")
    }

    override fun update(
        userId: String, id: Uuid, data: ReportUpdateReq
    ): ReportRes? = transaction {
        val report = getById(id) ?: return@transaction null
        if (report.user.id != userId) throw ForbiddenException("Not allowed")
        val rowsUpdated = ReportsTable.update({ ReportsTable.id eq id }) { row ->
            data.detail?.let { row[detail] = it }
        }
        if (rowsUpdated == 0) null else getById(id)
    }

    override fun delete(userId: String, id: Uuid): ReportRes? = transaction {
        val report = getById(id) ?: return@transaction null
        if (report.user.id != userId) throw ForbiddenException("Not allowed")
        val rowDeleted = ReportsTable.deleteWhere { ReportsTable.id eq id }
        if (rowDeleted == 0) null else report
    }
}