package com.ferosburn.project_management.screen.account

import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.user.UserRes

data class AccountScreenStates(
    val profile: AccountDataUiState = AccountDataUiState.Loading,
    val projects: AccountProjectsUiState = AccountProjectsUiState.Loading,
    val allProjects: AccountAllProjectsUiState = AccountAllProjectsUiState.Loading,
    val reports: AccountReportsUiState = AccountReportsUiState.Loading,
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

sealed interface AccountProjectsUiState {
    data object Loading : AccountProjectsUiState
    data class Success(val data: List<ProjectRes>) : AccountProjectsUiState
    data class Failed(val message: String) : AccountProjectsUiState
}

sealed interface AccountAllProjectsUiState {
    data object Loading : AccountAllProjectsUiState
    data class Success(val data: List<ProjectRes>) : AccountAllProjectsUiState
    data class Failed(val message: String) : AccountAllProjectsUiState
}

sealed interface AccountReportsUiState {
    data object Loading : AccountReportsUiState
    data class Success(val data: List<ReportRes>) : AccountReportsUiState
    data class Failed(val message: String) : AccountReportsUiState
}

data class AccountReportManageStates(
    val detail: String = "",
    val detailError: String? = null,
    val project: ProjectRes? = null,
    val projectError: String? = null,
    val projectDropdownExpanded: Boolean = false,
    val showFormDialog: Boolean = false,
    val currentReportId: String? = null,
    val isEdit: Boolean = false,
    val showDeleteDialog: Boolean = false,
)