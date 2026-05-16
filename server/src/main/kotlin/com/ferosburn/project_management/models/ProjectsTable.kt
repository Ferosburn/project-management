@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.models

import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

object ProjectsTable : Table("projects") {
    val id = uuid("id").uniqueIndex()
    val name = varchar("name", 256)
    val description = varchar("description", 512)
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(id)
}