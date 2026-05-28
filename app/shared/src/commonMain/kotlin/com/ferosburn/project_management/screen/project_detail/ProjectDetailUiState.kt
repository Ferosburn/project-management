package com.ferosburn.project_management.screen.project_detail

import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.task.TaskRes


data class ProjectDetailUiState(
    val projectData: ProjectDataUiState = ProjectDataUiState.Loading,
    val tasks: TasksUiState = TasksUiState.Loading,
    val taskCreate: TaskCreateUiState = TaskCreateUiState.Initial,
    val tabs: Set<ProjectDetailTab> = emptySet(),
    val selectedTab: ProjectDetailTab? = null,
    val reports: ProjectReportsUiState = ProjectReportsUiState.Loading,
    val task: TaskDetailUiState = TaskDetailUiState.Loading,
)

sealed interface TaskCreateUiState {
    data object Initial : TaskCreateUiState
    data object Loading : TaskCreateUiState
    data object Success : TaskCreateUiState
    data class Failed(val message: String) : TaskCreateUiState
}

sealed interface ProjectDataUiState {
    data object Loading : ProjectDataUiState
    data class Success(val data: ProjectRes) : ProjectDataUiState
    data class Failed(val message: String) : ProjectDataUiState
}

sealed interface TasksUiState {
    data object Loading : TasksUiState
    data class Success(val data: List<TaskRes>) : TasksUiState
    data class Failed(val message: String) : TasksUiState
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