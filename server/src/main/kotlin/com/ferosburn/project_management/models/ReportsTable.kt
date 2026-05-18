@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.models

import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

object ReportsTable : Table("reports") {
    val id = uuid("id")
    val userId = uuid("user_id").references(UsersTable.id).index()
    val projectId = uuid("project_id").references(ProjectsTable.id).index()
    val detail = varchar("detail", 512)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}