@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.auth.AuthChangePasswordReq
import com.ferosburn.project_management.models.auth.AuthLoginReq
import com.ferosburn.project_management.models.auth.AuthRefreshTokenReq
import com.ferosburn.project_management.models.auth.AuthTokenRes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AuthRepositoryInterface {
    fun login(data: AuthLoginReq): AuthTokenRes
    fun refreshToken(data: AuthRefreshTokenReq): AuthTokenRes?
    fun logout(userId: Uuid): Boolean
    fun changePassword(userId: Uuid, data: AuthChangePasswordReq): Boolean
}
