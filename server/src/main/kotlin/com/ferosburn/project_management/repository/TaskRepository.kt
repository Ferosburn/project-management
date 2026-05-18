@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.TasksTable
import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes
import com.ferosburn.project_management.models.task.TaskUpdateReq
import com.ferosburn.project_management.utils.InternalServerException
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TaskRepository : TaskRepositoryInterface {
    private fun mapToTaskRes(row: ResultRow): TaskRes {
        return TaskRes(
            id = row[TasksTable.id].toString(),
            name = row[TasksTable.name],
            description = row[TasksTable.description],
            status = row[TasksTable.status],
            parentId = row[TasksTable.parentId]?.toString(),
            rootParentId = row[TasksTable.rootParentId]?.toString(),
        )
    }

    private fun buildTaskTree(tasks: List<TaskRes>, parentId: String? = null): List<TaskRes> {
        val grouped = tasks.groupBy { it.parentId }
        return buildFromGroup(grouped, parentId)
    }

    private fun buildFromGroup(
        grouped: Map<String?, List<TaskRes>>, parentId: String?
    ): List<TaskRes> {
        return (grouped[parentId] ?: emptyList()).map { task ->
            task.copy(tasks = buildFromGroup(grouped, task.id))
        }
    }

    override fun getAllByProjectId(projectId: Uuid, filter: TaskFilter): List<TaskRes> = transaction {
        val rootTasks = TasksTable.selectAll().apply {
            orderBy(TasksTable.id to SortOrder.DESC)
            andWhere { TasksTable.projectId eq projectId }
            andWhere { TasksTable.parentId.isNull() }
            offset(((filter.page - 1) * filter.limit).toLong())
            limit(filter.limit)
        }
        val rootIds = rootTasks.map { it[TasksTable.id] }
        val descendants =
            TasksTable.selectAll().where { TasksTable.rootParentId inList rootIds }.map { mapToTaskRes(it) }
        rootTasks.map { mapToTaskRes(it) }.map { root ->
            root.copy(tasks = buildTaskTree(descendants, root.id))
        }
    }

    override fun getById(taskId: Uuid): TaskRes? = transaction {
        TasksTable.selectAll().where { TasksTable.id eq taskId }.map { mapToTaskRes(it) }.firstOrNull()
    }

    override fun create(projectId: Uuid, data: TaskCreateReq): TaskRes = transaction {
        val insertStatement = TasksTable.insert { row ->
            row[id] = Uuid.generateV7()
            row[TasksTable.projectId] = projectId
            row[parentId] = data.parentId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
            row[rootParentId] = data.rootParentId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
            row[name] = data.name
            row[description] = data.description
        }
        insertStatement.resultedValues?.singleOrNull()?.let { mapToTaskRes(it) }
            ?: throw InternalServerException("Failed to create task")
    }

    override fun update(taskId: Uuid, data: TaskUpdateReq): TaskRes? = transaction {
        val rowsUpdated = TasksTable.update({ TasksTable.id eq taskId }) { row ->
            data.name?.let { row[TasksTable.name] = it }
            data.description?.let { row[TasksTable.description] = it }
        }
        if (rowsUpdated == 0) null else getById(taskId)
    }

    override fun delete(taskId: Uuid): TaskRes? = transaction {
        val task = getById(taskId) ?: return@transaction null
        val rowDeleted = TasksTable.deleteWhere { TasksTable.id eq taskId }
        if (rowDeleted == 0) null else task
    }
}