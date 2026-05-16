package com.ferosburn.project_management.screen.projects

import com.ferosburn.project_management.models.project.ProjectRes

sealed interface ProjectsUiState {
    data object Loading : ProjectsUiState
    data class Success(val data: List<ProjectRes>) : ProjectsUiState
    data class Failed(val message: String) : ProjectsUiState
}