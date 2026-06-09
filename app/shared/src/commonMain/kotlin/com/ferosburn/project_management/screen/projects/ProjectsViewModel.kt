package com.ferosburn.project_management.screen.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.network.ProjectApi
import com.ferosburn.project_management.paging.AppPagingSource
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.flow.Flow

class ProjectsViewModel : ViewModel() {
    val currentRole = LocalStorage.readUserRole()
    val isManager = currentRole != null && currentRole == UserRole.MANAGER

    val getPagedProjects: Flow<PagingData<ProjectRes>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false), pagingSourceFactory = {
            AppPagingSource { limit, page -> ProjectApi.getAllProjects(ProjectFilter(limit, page)) }
        }).flow.cachedIn(viewModelScope)
}