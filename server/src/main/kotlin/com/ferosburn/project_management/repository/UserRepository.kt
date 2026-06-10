@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.PageableResult
import com.ferosburn.project_management.models.ReportsTable
import com.ferosburn.project_management.models.UsersTable
import com.ferosburn.project_management.models.user.*
import com.ferosburn.project_management.utils.BadRequestException
import com.ferosburn.project_management.utils.InternalServerException
import com.ferosburn.project_management.utils.PasswordUtils
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.countDistinct
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.neq
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

    override fun getAll(filter: UserFilter, userRole: UserRole, userId: Uuid): PageableResult<UserRes> {
        val query = UsersTable.selectAll().apply {
            when (userRole) {
                UserRole.SUPERADMIN -> andWhere { UsersTable.id neq userId }
                else -> andWhere { UsersTable.role neq UserRole.SUPERADMIN }
            }
            filter.role?.let { andWhere { UsersTable.role eq it } }
            filter.division?.let { andWhere { UsersTable.division eq it } }
            filter.isActive?.let { andWhere { UsersTable.isActive eq it } }
        }
        val totalCount = query.count()
        val users = query.apply {
            limit(filter.limit)
            offset(((filter.page - 1) * filter.limit).toLong())
            orderBy(UsersTable.isActive to SortOrder.DESC)
            orderBy(UsersTable.name to SortOrder.ASC)
        }.map { mapToUserRes(it) }
        return PageableResult(users, totalCount)
    }

    override fun getContributors(projectId: Uuid, filter: UserFilter): PageableResult<UserRes> {
        val totalCount = UsersTable.innerJoin(ReportsTable, { UsersTable.id }, { ReportsTable.userId })
            .select(UsersTable.id.countDistinct()).where { ReportsTable.projectId eq projectId }
            .single()[UsersTable.id.countDistinct()]
        val users =
            UsersTable.innerJoin(ReportsTable, { UsersTable.id }, { ReportsTable.userId }).select(UsersTable.columns)
                .withDistinct().where { ReportsTable.projectId eq projectId }.limit(filter.limit)
                .offset(((filter.page - 1) * filter.limit).toLong()).orderBy(UsersTable.isActive to SortOrder.DESC)
                .orderBy(UsersTable.name to SortOrder.ASC).map { mapToUserRes(it) }
        return PageableResult(users, totalCount)
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
            data.role?.let {
                val role = UserRole.valueOf(it)
                row[UsersTable.role] = role
                if (role != UserRole.DEVELOPER) row[division] = null
            }
            data.division?.let { row[division] = UserDivision.valueOf(it) }
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