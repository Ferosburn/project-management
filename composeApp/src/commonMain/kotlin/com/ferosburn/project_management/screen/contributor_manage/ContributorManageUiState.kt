package com.ferosburn.project_management.screen.contributor_manage

sealed interface ContributorManageUiState {
    data object Initial : ContributorManageUiState
    data object Loading : ContributorManageUiState
    data object Success : ContributorManageUiState
    data class Failed(val message: String) : ContributorManageUiState
}