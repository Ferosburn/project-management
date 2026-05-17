package com.ferosburn.project_management.screen.project_manage

sealed interface ProjectManageUiState {
    data object Initial : ProjectManageUiState
    data object Loading : ProjectManageUiState
    data object Success : ProjectManageUiState
    data class Failed(val message: String) : ProjectManageUiState
}