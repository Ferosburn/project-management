@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes
import com.ferosburn.project_management.models.task.TaskUpdateReq
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface TaskRepositoryInterface {
    fun getAllByProjectId(projectId: Uuid, filter: TaskFilter): List<TaskRes>
    fun getById(taskId: Uuid): TaskRes?
    fun create(projectId: Uuid, data: TaskCreateReq): TaskRes
    fun update(taskId: Uuid, data: TaskUpdateReq): TaskRes?
    fun delete(taskId: Uuid): TaskRes?
}