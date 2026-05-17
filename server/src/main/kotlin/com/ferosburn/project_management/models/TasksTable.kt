@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.models

import com.ferosburn.project_management.models.task.TaskStatus
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

object TasksTable : Table("tasks") {
    val id = uuid("id")
    val projectId = uuid("project_id").references(ProjectsTable.id).index()
    val parentId = uuid("parent_id").references(id, onDelete = ReferenceOption.CASCADE).nullable().index()
    val rootParentId = uuid("root_parent_id").references(id, onDelete = ReferenceOption.CASCADE).nullable().index()
    val name = varchar("name", 128)
    val description = varchar("description", 512)
    val status = enumerationByName("status", length = 20, TaskStatus::class).default(TaskStatus.BACKLOG)
    /*val isActive = bool("is_active").default(true)
    val deadlineAt = long("deadline_at").nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")*/

    override val primaryKey = PrimaryKey(id)
}