package com.ferosburn.project_management.screen.project_detail

import com.ferosburn.project_management.models.project.ProjectRes

sealed interface ProjectDetailUiState {
    data object Loading : ProjectDetailUiState
    data class Success(val data: ProjectRes) : ProjectDetailUiState
    data class Failed(val message: String) : ProjectDetailUiState
}