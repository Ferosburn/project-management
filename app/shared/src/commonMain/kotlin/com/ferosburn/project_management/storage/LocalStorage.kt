package com.ferosburn.project_management.storage

import com.ferosburn.project_management.models.user.UserRole
import com.russhwolf.settings.Settings

object LocalStorage : LocalStorageInterface {
    private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
    private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
    private const val USER_ROLE_KEY = "USER_ROLE"
    private const val USER_ID_KEY = "USER_ID"
    private val settings: Settings = Settings()

    override fun saveAccessToken(token: String) = settings.putString(ACCESS_TOKEN_KEY, token)

    override fun readAccessToken(): String? = settings.getStringOrNull(ACCESS_TOKEN_KEY)

    override fun deleteAccessToken() = settings.remove(ACCESS_TOKEN_KEY)

    override fun saveRefreshToken(token: String) = settings.putString(REFRESH_TOKEN_KEY, token)

    override fun readRefreshToken(): String? = settings.getStringOrNull(REFRESH_TOKEN_KEY)

    override fun deleteRefreshToken() = settings.remove(REFRESH_TOKEN_KEY)

    override fun deleteTokenRes() {
        deleteAccessToken()
        deleteRefreshToken()
        deleteUserRole()
        deleteUserId()
    }

    override fun saveUserRole(role: UserRole) = settings.putString(USER_ROLE_KEY, role.name)

    override fun readUserRole(): UserRole? {
        val roleString = settings.getStringOrNull(USER_ROLE_KEY)
        return runCatching { roleString?.let { UserRole.valueOf(it) } }.getOrNull()
    }

    override fun deleteUserRole() = settings.remove(USER_ROLE_KEY)

    override fun saveUserId(userId: String) = settings.putString(USER_ID_KEY, userId)

    override fun readUserId(): String? = settings.getStringOrNull(USER_ID_KEY)

    override fun deleteUserId() = settings.remove(USER_ID_KEY)

    override fun deleteAll() = settings.clear()
}