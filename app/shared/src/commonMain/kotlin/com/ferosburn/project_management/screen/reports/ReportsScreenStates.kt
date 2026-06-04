package com.ferosburn.project_management.screen.reports

import com.ferosburn.project_management.models.project.ProjectRes

data class ReportsScreenStates(
    val reportScreen: ReportsScreenUiState = ReportsScreenUiState.Initial,
    val projects: ReportProjectsUiState = ReportProjectsUiState.Loading
)

sealed interface ReportsScreenUiState {
    data object Initial : ReportsScreenUiState
    data object Loading : ReportsScreenUiState
    data object Success : ReportsScreenUiState
    data class Failed(val message: String) : ReportsScreenUiState
}

sealed interface ReportProjectsUiState {
    data object Loading : ReportProjectsUiState
    data class Success(val data: List<ProjectRes>) : ReportProjectsUiState
    data class Failed(val message: String) : ReportProjectsUiState
}
