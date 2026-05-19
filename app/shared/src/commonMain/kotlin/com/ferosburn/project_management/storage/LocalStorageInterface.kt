package com.ferosburn.project_management.storage

import com.ferosburn.project_management.models.user.UserRole

interface LocalStorageInterface {
    fun saveAccessToken(token: String)
    fun readAccessToken(): String?
    fun deleteAccessToken()
    fun saveRefreshToken(token: String)
    fun readRefreshToken(): String?
    fun deleteRefreshToken()
    fun deleteTokenRes()
    fun saveUserRole(role: UserRole)
    fun readUserRole(): UserRole?
    fun deleteUserRole()
    fun saveUserId(userId: String)
    fun readUserId(): String?
    fun deleteUserId()
    fun deleteAll()
}