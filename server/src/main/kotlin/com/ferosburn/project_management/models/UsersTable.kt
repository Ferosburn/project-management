@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.models

import com.ferosburn.project_management.models.user.UserDivision
import com.ferosburn.project_management.models.user.UserRole
import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

object UsersTable : Table("users") {
    val id = uuid("id").uniqueIndex()
    val username = varchar("username", 128).uniqueIndex()
    val name = varchar("name", 128)
    val passwordHash = varchar("password_hash", 128)
    val role = enumerationByName("role", 20, UserRole::class)
    val division = enumerationByName("division", 20, UserDivision::class).nullable()
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(id)
}