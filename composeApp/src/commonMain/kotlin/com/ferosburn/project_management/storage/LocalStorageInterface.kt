package com.ferosburn.project_management.storage

interface LocalStorageInterface {
    fun saveAccessToken(token: String)
    fun readAccessToken(): String?
    fun deleteAccessToken()

    fun saveRefreshToken(token: String)
    fun readRefreshToken(): String?
    fun deleteRefreshToken()

    fun deleteTokens()

    fun deleteAll()
}