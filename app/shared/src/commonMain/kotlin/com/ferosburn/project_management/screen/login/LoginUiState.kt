package com.ferosburn.project_management.screen.login

sealed interface LoginUiState {
    data object Initial : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}