package com.ferosburn.project_management.screen.contributors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.network.UserApi
import com.ferosburn.project_management.paging.AppPagingSource
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.flow.Flow

class ContributorsViewModel : ViewModel() {
    val currentRole = LocalStorage.readUserRole()
    val isSuperAdmin = currentRole != null && currentRole == UserRole.SUPERADMIN

    val getPagedUsers: Flow<PagingData<UserRes>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { AppPagingSource { limit, page -> UserApi.getAllUsers(UserFilter(limit, page)) } },
    ).flow.cachedIn(viewModelScope)
}