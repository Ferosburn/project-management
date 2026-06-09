@file:OptIn(ExperimentalUuidApi::class)

package com.ferosburn.project_management.repository

import com.ferosburn.project_management.models.PageableResult
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ProjectRepositoryInterface {
    fun getAll(filter: ProjectFilter = ProjectFilter()): PageableResult<ProjectRes>
    fun getById(id: Uuid): ProjectRes?
    fun create(data: ProjectCreateReq): ProjectRes
    fun update(id: Uuid, data: ProjectUpdateReq): ProjectRes?
    fun delete(id: Uuid): ProjectRes?
}