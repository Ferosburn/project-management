package com.ferosburn.project_management.screen.project_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.report.ReportUpdateReq
import com.ferosburn.project_management.models.task.*
import com.ferosburn.project_management.models.user.UserFilter
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import com.ferosburn.project_management.network.ReportApi
import com.ferosburn.project_management.network.TaskApi
import com.ferosburn.project_management.paging.AppPagingSource
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProjectDetailViewModel : ViewModel() {
    private val _uiStates = MutableStateFlow(ProjectDetailUiState())
    val uiStates = _uiStates.asStateFlow()
    val currentRole = LocalStorage.readUserRole()
    val userId = LocalStorage.readUserId()
    val isManager = currentRole != null && currentRole == UserRole.MANAGER
    val taskDelegate = ProjectTaskDelegate()
    val tabDelegate = ProjectTabDelegate()
    val reportDelegate = ProjectReportDelegate()
    private var currentReportPagingSource: AppPagingSource<ReportRes>? = null
    private var currentTaskPagingSource: AppPagingSource<TaskRes>? = null

    fun init(projectId: String, isExpanded: Boolean) {
        getProjectDetail(projectId = projectId)
        tabDelegate.updateTabs(
            tabs = setOf(ProjectDetailTab.REPORTS, ProjectDetailTab.CONTRIBUTORS),
            activeTab = if (isExpanded) ProjectDetailTab.REPORTS else null,
        )
    }

    fun openTaskDetail(projectId: String, taskId: String) {
        val tabs = tabDelegate.state.value.tabs
        tabDelegate.updateTabs(
            tabs = listOf(tabs, listOf(ProjectDetailTab.DETAIL)).flatten().toSet(),
            activeTab = ProjectDetailTab.DETAIL,
            expanded = true,
        )
        taskDelegate.updateCurrentTaskId(taskId)
        getTaskDetail(projectId)
    }

    fun closeTaskDetail(isWindowExpanded: Boolean) {
        tabDelegate.updateTabs(
            tabs = tabDelegate.state.value.tabs.filter { tab -> tab != ProjectDetailTab.DETAIL }.toSet(),
            activeTab = if (isWindowExpanded) ProjectDetailTab.REPORTS else null,
        )
        taskDelegate.updateCurrentTaskId(null)
    }

    suspend fun <T> handleOverlayState(
        response: NetworkResult<T>, mainAction: () -> Unit, action: () -> Unit
    ) {
        when (response) {
            is NetworkResult.Success -> {
                action()
                _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Success) }
                mainAction()
            }

            is NetworkResult.Error -> {
                action()
                _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Failed(response.message)) }
                delay(3.seconds)
                _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Initial) }
            }
        }
    }

    fun getProjectDetail(projectId: String) {
        viewModelScope.launch {
            _uiStates.update { it.copy(projectData = ProjectDataUiState.Loading) }
            when (val response = ProjectApi.getProjectDetail(projectId)) {
                is NetworkResult.Error -> _uiStates.update { it.copy(projectData = ProjectDataUiState.Failed(response.message)) }
                is NetworkResult.Success -> {
                    response.data.data?.let { data ->
                        _uiStates.update { it.copy(projectData = ProjectDataUiState.Success(data)) }
                    }
                }
            }
        }
    }

    fun getPagedContributors(projectId: String): Flow<PagingData<UserRes>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            AppPagingSource { limit, page -> ProjectApi.getProjectContributors(projectId, UserFilter(limit, page)) }
        },
    ).flow.cachedIn(viewModelScope)

    fun getPagedTasks(projectId: String): Flow<PagingData<TaskRes>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            AppPagingSource { limit, page -> TaskApi.getAllTasks(projectId, TaskFilter(limit, page)) }.also {
                currentTaskPagingSource = it
            }
        },
    ).flow.cachedIn(viewModelScope)

    fun getTaskDetail(projectId: String) {
        viewModelScope.launch {
            val currentTaskId = taskDelegate.state.value.currentTaskId
            if (currentTaskId.isNullOrBlank()) {
                _uiStates.update { it.copy(task = TaskDetailUiState.Failed("taskId is required")) }
                return@launch
            }
            _uiStates.update { it.copy(task = TaskDetailUiState.Loading) }
            when (val response = TaskApi.getTask(projectId, currentTaskId)) {
                is NetworkResult.Error -> _uiStates.update { it.copy(task = TaskDetailUiState.Failed(response.message)) }
                is NetworkResult.Success -> {
                    response.data.data?.let { data ->
                        _uiStates.update { it.copy(task = TaskDetailUiState.Success(data)) }
                    }
                }
            }
        }
    }

    fun createTask(projectId: String) {
        if (!taskDelegate.validateForm()) return
        viewModelScope.launch {
            _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Loading) }
            val taskState = taskDelegate.state.value
            val response = TaskApi.createTask(
                projectId,
                TaskCreateReq(
                    name = taskState.name,
                    description = taskState.description,
                    parentId = taskState.currentParentId,
                    rootParentId = taskState.currentRootParentId
                ),
            )
            handleOverlayState(response, { currentTaskPagingSource?.invalidate() }, taskDelegate::closeTaskFormDialog)
        }
    }

    fun updateTask(projectId: String) {
        if (!taskDelegate.validateForm()) return
        viewModelScope.launch {
            _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Loading) }
            val taskState = taskDelegate.state.value
            val response = TaskApi.updateTask(
                projectId,
                taskState.currentTaskId!!,
                TaskUpdateReq(
                    name = taskState.name,
                    description = taskState.description,
                ),
            )
            handleOverlayState(response, { currentTaskPagingSource?.invalidate() }, taskDelegate::closeTaskFormDialog)
        }
    }

    fun updateTaskStatus(projectId: String, taskId: String, newStatus: TaskStatus, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Loading) }
            val response = TaskApi.updateTask(
                projectId,
                taskId,
                TaskUpdateReq(
                    status = newStatus.name,
                ),
            )
            handleOverlayState(response, { onSuccess() }, { })
        }
    }

    fun deleteTask(projectId: String) {
        taskDelegate.closeDeleteTaskDialog()
        viewModelScope.launch {
            _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Loading) }
            val response = TaskApi.deleteTask(
                projectId,
                taskDelegate.state.value.currentTaskId!!,
            )
            handleOverlayState(
                response, { currentTaskPagingSource?.invalidate() }, { taskDelegate.updateCurrentTaskId(null) },
            )
        }
    }

    fun getPagedReports(projectId: String): Flow<PagingData<ReportRes>> = Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            AppPagingSource { limit, page ->
                ReportApi.getAllReports(ReportFilter(limit, page, projectId))
            }.also { currentReportPagingSource = it }
        },
    ).flow.cachedIn(viewModelScope)

    fun createReport(projectId: String) {
        if (!reportDelegate.validateForm()) return
        viewModelScope.launch {
            _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Loading) }
            val response = ReportApi.createReport(
                ReportCreateReq(projectId = projectId, detail = reportDelegate.state.value.detail)
            )
            handleOverlayState(
                response, { currentReportPagingSource?.invalidate() }, reportDelegate::closeReportFormDialog
            )
        }
    }

    fun updateReport() {
        if (!reportDelegate.validateForm()) return
        viewModelScope.launch {
            _uiStates.update { it.copy(overlay = ProjectDetailOverlayUiState.Loading) }
            val reportState = reportDelegate.state.value
            val response = ReportApi.updateReport(
                reportState.currentReportId!!, ReportUpdateReq(detail = reportDelegate.state.value.detail)
            )
            handleOverlayState(
                response, { currentReportPagingSource?.invalidate() }, reportDelegate::closeReportFormDialog
            )
        }
    }

    fun deleteReport() {
        reportDelegate.closeDeleteReportDialog()
        viewModelScope.launch {
            _uiStates.update { it.copy(reports = ProjectReportsUiState.Loading) }
            val response = ReportApi.deleteReport(reportDelegate.state.value.currentReportId!!)
            handleOverlayState(
                response,
                { currentReportPagingSource?.invalidate() },
                { reportDelegate.updateCurrentReportId(null) },
            )
        }
    }
}