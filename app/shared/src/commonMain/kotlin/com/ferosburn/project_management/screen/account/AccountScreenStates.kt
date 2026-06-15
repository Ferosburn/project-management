package com.ferosburn.project_management.screen.account

import com.ferosburn.project_management.models.user.UserRes

data class AccountScreenStates(
    val profile: AccountDataUiState = AccountDataUiState.Loading,
    val overlay: AccountOverlayUiState = AccountOverlayUiState.Initial,
)

sealed interface AccountOverlayUiState {
    data object Initial : AccountOverlayUiState
    data object Loading : AccountOverlayUiState
    data object SuccessLogout : AccountOverlayUiState
    data class Failed(val message: String) : AccountOverlayUiState
}

sealed interface AccountDataUiState {
    data object Loading : AccountDataUiState
    data class Success(val data: UserRes) : AccountDataUiState
    data class Failed(val message: String) : AccountDataUiState
}