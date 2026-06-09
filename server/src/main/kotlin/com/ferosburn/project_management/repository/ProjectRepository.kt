@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.PageableResult
import com.ferosburn.project_management.models.ProjectsTable
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq
import com.ferosburn.project_management.utils.InternalServerException
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProjectRepository : ProjectRepositoryInterface {
    fun mapToProjectRes(row: ResultRow): ProjectRes {
        return ProjectRes(
            id = row[ProjectsTable.id].toString(),
            name = row[ProjectsTable.name],
            description = row[ProjectsTable.description],
            isActive = row[ProjectsTable.isActive],
        )
    }

    override fun getAll(filter: ProjectFilter): PageableResult<ProjectRes> {
        val query = ProjectsTable.selectAll()
        filter.isActive?.let { query.andWhere { ProjectsTable.isActive eq it } }
        val count = query.count()
        val projects = query.apply {
            limit(filter.limit)
            offset(((filter.page - 1) * filter.limit).toLong())
            orderBy(ProjectsTable.id to SortOrder.DESC)
        }.map { mapToProjectRes(it) }
        return PageableResult(projects, count)
    }

    override fun getById(id: Uuid): ProjectRes? = transaction {
        ProjectsTable.selectAll().where { ProjectsTable.id eq id }.map { mapToProjectRes(it) }.firstOrNull()
    }

    override fun create(data: ProjectCreateReq): ProjectRes = transaction {
        val insertStatement = ProjectsTable.insert { row ->
            row[id] = Uuid.generateV7()
            row[name] = data.name
            row[description] = data.description
        }
        insertStatement.resultedValues?.singleOrNull()?.let { mapToProjectRes(it) }
            ?: throw InternalServerException("Failed to create project")
    }

    override fun update(id: Uuid, data: ProjectUpdateReq): ProjectRes? = transaction {
        val rowsUpdated = ProjectsTable.update({ ProjectsTable.id eq id }) { row ->
            data.name?.let { row[name] = it }
            data.description?.let { row[description] = it }
            data.isActive?.let { row[isActive] = it }
        }
        if (rowsUpdated == 0) null else getById(id)
    }

    override fun delete(id: Uuid): ProjectRes? = transaction {
        val project = getById(id) ?: return@transaction null
        val rowDeleted = ProjectsTable.deleteWhere { ProjectsTable.id eq id }
        if (rowDeleted == 0) null else project
    }
}