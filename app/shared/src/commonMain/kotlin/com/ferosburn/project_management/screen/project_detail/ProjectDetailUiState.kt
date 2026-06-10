package com.ferosburn.project_management.screen.project_detail

import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.task.TaskRes


data class ProjectDetailUiState(
    val projectData: ProjectDataUiState = ProjectDataUiState.Loading,
    val overlay: ProjectDetailOverlayUiState = ProjectDetailOverlayUiState.Initial,
    val reports: ProjectReportsUiState = ProjectReportsUiState.Loading,
    val task: TaskDetailUiState = TaskDetailUiState.Loading,
)

sealed interface ProjectDetailOverlayUiState {
    data object Initial : ProjectDetailOverlayUiState
    data object Loading : ProjectDetailOverlayUiState
    data object Success : ProjectDetailOverlayUiState
    data class Failed(val message: String) : ProjectDetailOverlayUiState
}

sealed interface ProjectDataUiState {
    data object Loading : ProjectDataUiState
    data class Success(val data: ProjectRes) : ProjectDataUiState
    data class Failed(val message: String) : ProjectDataUiState
}

sealed interface ProjectReportsUiState {
    data object Loading : ProjectReportsUiState
    data class Success(val data: List<ReportRes>) : ProjectReportsUiState
    data class Failed(val message: String) : ProjectReportsUiState
}

sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState
    data class Success(val data: TaskRes) : TaskDetailUiState
    data class Failed(val message: String) : TaskDetailUiState
}

data class ProjectTabManageStates(
    val tabs: Set<ProjectDetailTab> = emptySet(),
    val activeTab: ProjectDetailTab? = null,
    val expanded: Boolean = false,
)

data class ProjectTaskManageStates(
    val name: String = "",
    val description: String = "",
    val nameError: String? = null,
    val descriptionError: String? = null,
    val showFormDialog: Boolean = false,
    val isEdit: Boolean = false,
    val currentTaskId: String? = null,
    val currentParentId: String? = null,
    val currentRootParentId: String? = null,
    val showDeleteDialog: Boolean = false,
)

data class ProjectReportManageStates(
    val detail: String = "",
    val detailError: String? = null,
    val showFormDialog: Boolean = false,
    val currentReportId: String? = null,
    val isEdit: Boolean = false,
    val showDeleteDialog: Boolean = false,
)