package com.ferosburn.project_management.storage

import com.russhwolf.settings.Settings

object LocalStorage : LocalStorageInterface {
    private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
    private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
    private val settings: Settings = Settings()

    override fun saveAccessToken(token: String) = settings.putString(ACCESS_TOKEN_KEY, token)

    override fun readAccessToken(): String? = settings.getStringOrNull(ACCESS_TOKEN_KEY)

    override fun deleteAccessToken() = settings.remove(ACCESS_TOKEN_KEY)

    override fun saveRefreshToken(token: String) = settings.putString(REFRESH_TOKEN_KEY, token)

    override fun readRefreshToken(): String? = settings.getStringOrNull(REFRESH_TOKEN_KEY)

    override fun deleteRefreshToken() = settings.remove(REFRESH_TOKEN_KEY)

    override fun deleteTokens() {
        deleteAccessToken()
        deleteRefreshToken()
    }

    override fun deleteAll() = settings.clear()
}