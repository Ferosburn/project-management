@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.RefreshTokensTable
import com.ferosburn.project_management.models.UsersTable
import com.ferosburn.project_management.models.auth.AuthChangePasswordReq
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.models.auth.AuthTokenRes
import com.ferosburn.project_management.utils.ForbiddenException
import com.ferosburn.project_management.utils.JWTUtils
import com.ferosburn.project_management.utils.NotFoundException
import com.ferosburn.project_management.utils.PasswordUtils
import com.ferosburn.project_management.utils.UnauthorizedException
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthRepository : AuthRepositoryInterface {
    override fun login(data: AuthLoginReq): AuthTokenRes = transaction {
        val user = UsersTable.selectAll().where { UsersTable.username eq data.username }.singleOrNull()
            ?: throw UnauthorizedException("Invalid username or password")

        val isPasswordValid = PasswordUtils.verify(data.password, user[UsersTable.passwordHash])
        if (!isPasswordValid) throw UnauthorizedException("Invalid username or password")

        if (!user[UsersTable.isActive]) throw ForbiddenException("Account is deactivated")

        val userId = user[UsersTable.id]
        val role = user[UsersTable.role]

        val accessToken = JWTUtils.generateAccessToken(userId, role)
        val refreshToken = JWTUtils.generateRefreshToken(userId)

        val refreshExpiry = System.getenv("JWT_REFRESH_EXPIRY")?.toLong() ?: 604800
        RefreshTokensTable.deleteWhere { RefreshTokensTable.userId eq userId }
        RefreshTokensTable.insert {
            it[id] = Uuid.generateV7()
            it[RefreshTokensTable.userId] = userId
            it[token] = refreshToken
            it[expiresAt] = System.currentTimeMillis() + refreshExpiry * 1000
            it[createdAt] = System.currentTimeMillis()
        }
        AuthTokenRes(accessToken, refreshToken, role)
    }

    override fun refreshToken(data: AuthRefreshTokenReq): AuthTokenRes? = transaction {
        val decodedToken = JWTUtils.verifyRefreshToken(data.refreshToken) ?: return@transaction null
        RefreshTokensTable.selectAll().where { RefreshTokensTable.token eq data.refreshToken }.none()
            .let { if (it) return@transaction null }
        val userId = Uuid.parse(decodedToken.getClaim("userId").asString())
        val user = UsersTable.selectAll().where { UsersTable.id eq userId }.singleOrNull() ?: return@transaction null
        if (!user[UsersTable.isActive]) return@transaction null
        val role = user[UsersTable.role]
        val newAccessToken = JWTUtils.generateAccessToken(userId, role)
        AuthTokenRes(newAccessToken, data.refreshToken, role)
    }

    override fun logout(userId: Uuid): Boolean = transaction {
        val tokens = RefreshTokensTable.deleteWhere { RefreshTokensTable.userId eq userId }
        tokens > 0
    }

    override fun changePassword(
        userId: Uuid, data: AuthChangePasswordReq
    ): Boolean = transaction {
        val user = UsersTable.selectAll().where { UsersTable.id eq userId }.singleOrNull() ?: throw NotFoundException("User not found")

        val isPasswordValid = PasswordUtils.verify(data.oldPassword, user[UsersTable.passwordHash])
        if (!isPasswordValid) throw UnauthorizedException("Old password is incorrect")

        val newPassword = PasswordUtils.hash(data.newPassword)
        UsersTable.update({ UsersTable.id eq userId }) { it[passwordHash] = newPassword } > 0
    }
}