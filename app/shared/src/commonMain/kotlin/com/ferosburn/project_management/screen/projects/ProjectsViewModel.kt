package com.ferosburn.project_management.screen.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectsUiState>(ProjectsUiState.Loading)
    val uiState = _uiState.asStateFlow()
    val currentRole = LocalStorage.readUserRole()
    val isManager = currentRole != null && currentRole == UserRole.MANAGER

    fun getAllProjects(filter: ProjectFilter = ProjectFilter()) {
        viewModelScope.launch {
            _uiState.value = ProjectsUiState.Loading
            when (val response = ProjectApi.getAllProjects(filter)) {
                is NetworkResult.Error -> _uiState.value = ProjectsUiState.Failed(response.message)
                is NetworkResult.Success -> {
                    response.data.data.let {
                        _uiState.value = ProjectsUiState.Success(it ?: emptyList())
                    }
                }
            }
        }
    }
}