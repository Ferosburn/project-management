@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.database

import com.ferosburn.project_management.models.UsersTable
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.utils.PasswordUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun seedSuperAdmin() {
    val superAdminUsername = System.getenv("SUPERADMIN_USERNAME") ?: "superadmin"
    val superAdminPassword =
        System.getenv("SUPERADMIN_PASSWORD") ?: error("SUPERADMIN_PASSWORD environment variable not set")
    val superAdminName = System.getenv("SUPERADMIN_NAME") ?: "Super Admin"

    val exists = UsersTable.selectAll().where { UsersTable.role eq UserRole.SUPERADMIN }.count() > 0

    if (exists) {
        println("Seeder: SUPERADMIN already exists, skipping.")
        return
    }

    UsersTable.insert {
        it[id] = Uuid.generateV7()
        it[name] = superAdminName
        it[username] = superAdminUsername
        it[passwordHash] = PasswordUtils.hash(superAdminPassword)
        it[role] = UserRole.SUPERADMIN
        it[division] = null
        it[isActive] = true
    }
    println("Seeder: SUPERADMIN created successfully.")
}