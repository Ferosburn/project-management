package com.ferosburn.project_management.screen.project_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectDetailUiState>(ProjectDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getProjectDetail(projectId: String) {
        viewModelScope.launch {
            _uiState.value = ProjectDetailUiState.Loading
            when (val response = ProjectApi.getProjectDetail(projectId)) {
                is NetworkResult.Error -> _uiState.value = ProjectDetailUiState.Failed(response.message)
                is NetworkResult.Success -> {
                    response.data.data?.let {
                        ProjectDetailUiState.Success(it)
                    }
                }
            }
        }
    }
}