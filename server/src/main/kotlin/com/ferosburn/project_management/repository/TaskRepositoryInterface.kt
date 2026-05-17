@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.task.TaskFilter
import com.ferosburn.project_management.models.task.TaskRes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface TaskRepositoryInterface {
    fun getAllByProjectId(projectId: Uuid, filter: TaskFilter): List<TaskRes>
    /*fun getById()
    fun create()
    fun update()
    fun delete()*/
}