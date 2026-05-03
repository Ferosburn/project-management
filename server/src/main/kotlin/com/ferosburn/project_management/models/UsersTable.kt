package com.ferosburn.project_management.models

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 128).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)

    override val primaryKey = PrimaryKey(id)
}