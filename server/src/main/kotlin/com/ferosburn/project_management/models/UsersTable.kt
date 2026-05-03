package com.ferosburn.project_management.models

import org.jetbrains.exposed.v1.core.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128).uniqueIndex()
    val name = varchar("name", 128)
    val passwordHash = varchar("password_hash", 128)
    val role = enumerationByName("role", 20, Role::class)
    val division = enumerationByName("division", 20, Division::class).nullable()

    override val primaryKey = PrimaryKey(id)
}

enum class Role {
    DEVELOPER, MANAGER, SUPERADMIN, CLIENT
}

enum class Division {
    FRONTEND, UI, BACKEND, MOBILE, DEVOPS
}