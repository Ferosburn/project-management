@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.TasksTable
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
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
}