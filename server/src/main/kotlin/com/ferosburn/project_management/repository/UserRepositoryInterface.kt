@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.user.UserCreateReq
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.models.user.UserUpdateReq
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserRepositoryInterface {
    fun getAll(filter: UserFilter = UserFilter()): List<UserRes>
    fun getById(id: Uuid): UserRes?
    fun create(data: UserCreateReq): UserRes
    fun update(id: Uuid, data: UserUpdateReq): UserRes?
    fun delete(id: Uuid): UserRes?
}