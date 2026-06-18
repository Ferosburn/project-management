package com.ferosburn.project_management.plugins

import com.ferosburn.project_management.database.seedSuperAdmin
import com.ferosburn.project_management.models.ProjectsTable
import com.ferosburn.project_management.models.RefreshTokensTable
import com.ferosburn.project_management.models.ReportsTable
import com.ferosburn.project_management.models.TasksTable
import com.ferosburn.project_management.models.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun initDatabase() {
    val user = System.getenv("POSTGRES_USER") ?: error("POSTGRES_USER environment variable not set")
    val password = System.getenv("POSTGRES_PASSWORD") ?: error("POSTGRES_PASSWORD environment variable not set")
    val dbName = System.getenv("POSTGRES_DB") ?: error("POSTGRES_DB environment variable not set")
    val dbHost = System.getenv("DB_HOST") ?: error("DB_HOST environment variable not set")
    val dbPort = System.getenv("DB_PORT") ?: error("DB_PORT environment variable not set")
    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
        username = user
        this.password = password
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    val dataSource = HikariDataSource(config)

    Database.connect(dataSource)
    transaction {
        SchemaUtils.create(UsersTable, RefreshTokensTable, ProjectsTable, TasksTable, ReportsTable)
        SchemaUtils.addMissingColumnsStatements(
            UsersTable, RefreshTokensTable, ProjectsTable, TasksTable, ReportsTable
        ).also { statements ->
            if (statements.isNotEmpty()) {
                println("Applying ${statements.size} migration(s)...")
                statements.forEach { exec(it) }
            }
        }
        seedSuperAdmin()
    }
}