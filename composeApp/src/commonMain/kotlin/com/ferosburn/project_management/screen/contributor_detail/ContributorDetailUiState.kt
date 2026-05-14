package com.ferosburn.project_management.screen.contributor_detail

import com.ferosburn.project_management.models.user.UserRes

sealed interface ContributorDetailUiState {
    data object Loading : ContributorDetailUiState
    data class Success(val data: UserRes) : ContributorDetailUiState
    data class Failed(val message: String) : ContributorDetailUiState
}