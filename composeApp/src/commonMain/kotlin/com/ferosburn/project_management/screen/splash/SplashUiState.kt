package com.ferosburn.project_management.screen.splash

sealed interface SplashUiState {
    data object Initial : SplashUiState
    data object Success : SplashUiState
    data class Failed(val message: String) : SplashUiState
}