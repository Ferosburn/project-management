@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.UsersTable
import com.ferosburn.project_management.models.user.UserCreateReq
import com.ferosburn.project_management.models.user.UserDivision
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.models.user.UserUpdateReq
import com.ferosburn.project_management.utils.BadRequestException
import com.ferosburn.project_management.utils.InternalServerException
import com.ferosburn.project_management.utils.PasswordUtils
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRepository : UserRepositoryInterface {
    fun mapToUserRes(row: ResultRow): UserRes {
        return UserRes(
            id = row[UsersTable.id].toString(),
            name = row[UsersTable.name],
            username = row[UsersTable.username],
            role = row[UsersTable.role],
            division = row[UsersTable.division],
            isActive = row[UsersTable.isActive],
        )
    }

    override fun getAll(filter: UserFilter): List<UserRes> = transaction {
        UsersTable.selectAll().apply {
            filter.role?.let { andWhere { UsersTable.role eq it } }
            filter.division?.let { andWhere { UsersTable.division eq it } }
            filter.isActive?.let { andWhere { UsersTable.isActive eq it } }
            limit(filter.limit)
            offset(((filter.page - 1) * filter.limit).toLong())
        }.map { mapToUserRes(it) }
    }

    override fun getById(id: Uuid): UserRes? = transaction {
        UsersTable.selectAll().where { UsersTable.id eq id }.map { mapToUserRes(it) }.singleOrNull()
    }

    override fun create(data: UserCreateReq): UserRes = transaction {
        val insertStatement = UsersTable.insert { row ->
            row[id] = Uuid.generateV7()
            row[name] = data.name
            row[username] = data.username
            row[passwordHash] = PasswordUtils.hash(data.password)
            row[role] = data.role?.let { UserRole.valueOf(it) } ?: throw BadRequestException("Role is required")
            row[division] = data.division?.let { UserDivision.valueOf(it) }
        }
        insertStatement.resultedValues?.singleOrNull()?.let { mapToUserRes(it) }
            ?: throw InternalServerException("Failed to insert user")
    }

    override fun update(
        id: Uuid, data: UserUpdateReq,
    ): UserRes? = transaction {
        val rowsUpdated = UsersTable.update({ UsersTable.id eq id }) { row ->
            data.name?.let { row[name] = it }
            data.role?.let { row[role] = UserRole.valueOf(it) }
            row[division] = data.division?.let { UserDivision.valueOf(it) }
            data.isActive?.let { row[isActive] = it }
        }
        if (rowsUpdated == 0) null else getById(id)
    }

    override fun delete(id: Uuid): UserRes? = transaction {
        val user = getById(id) ?: return@transaction null
        val rowDeleted = UsersTable.deleteWhere { UsersTable.id eq id }
        if (rowDeleted == 0) null else user
    }
}