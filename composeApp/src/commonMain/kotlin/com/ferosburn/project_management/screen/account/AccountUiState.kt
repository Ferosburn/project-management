package com.ferosburn.project_management.screen.account

sealed interface AccountUiState {
    data object Initial : AccountUiState
    data object Loading : AccountUiState
    data object SuccessLogout : AccountUiState
    data class Failed(val message: String) : AccountUiState
}