package com.ferosburn.project_management.screen.contributors

import com.ferosburn.project_management.models.user.UserRes

sealed interface ContributorsUiState {
    data object Loading : ContributorsUiState
    data class Success(val data: List<UserRes>) : ContributorsUiState
    data class Failed(val message: String) : ContributorsUiState
}