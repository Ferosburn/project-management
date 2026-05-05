@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.models

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

object RefreshTokensTable : Table("refresh_tokens") {
    val id = uuid("id").uniqueIndex()
    val userId = uuid("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val token = varchar("token", 512)
    val expiresAt = long("expires_at")
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}