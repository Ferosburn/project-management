package com.ferosburn.project_management.screen.reports

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportFilter
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.models.report.ReportUpdateReq
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import com.ferosburn.project_management.network.ReportApi
import com.ferosburn.project_management.paging.AppPagingSource
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ReportsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReportsScreenStates())
    val uiState = _uiState.asStateFlow()
    val userId = LocalStorage.readUserId()
    var detail by mutableStateOf("")
    var project by mutableStateOf<ProjectRes?>(null)
    var detailError by mutableStateOf<String?>(null)
    var projectError by mutableStateOf<String?>(null)
    var projectDropdownExpanded by mutableStateOf(false)
    var showReportFormDialog by mutableStateOf(false)
    var isEditReport by mutableStateOf(false)
    var currentReportId by mutableStateOf<String?>(null)
    var showDeleteDialog by mutableStateOf(false)
    var countDownFinished by mutableStateOf(false)

    val getPagedReports: Flow<PagingData<ReportRes>> = Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false), pagingSourceFactory = {
            AppPagingSource { limit, page -> ReportApi.getAllReports(ReportFilter(limit, page)) }
        }).flow.cachedIn(viewModelScope)

    private fun validateForm(): Boolean {
        detailError = when {
            detail.isBlank() -> "detail is required"
            else -> null
        }
        if (!isEditReport) projectError = when {
            project == null -> "project is required"
            else -> null
        }
        return listOf(detailError, projectError).all { it.isNullOrBlank() }
    }

    fun onDetailChange(value: String) {
        detail = value
        detailError = null
    }

    fun onProjectChange(value: ProjectRes?) {
        project = value
        projectError = null
        projectDropdownExpanded = false
    }

    fun onProjectExpandChange() {
        projectDropdownExpanded = !projectDropdownExpanded
    }

    fun openCreateReportDialog() {
        isEditReport = false
        showReportFormDialog = true
    }

    fun openEditReportDialog(report: ReportRes) {
        isEditReport = true
        showReportFormDialog = true
        currentReportId = report.id
        detail = report.detail
    }

    fun closeReportFormDialog() {
        detail = ""
        detailError = null
        project = null
        projectError = null
        currentReportId = null
        showReportFormDialog = false
    }

    fun openDeleteReportDialog(reportId: String) {
        showDeleteDialog = true
        countDownFinished = false
        currentReportId = reportId
    }

    fun closeDeleteReportDialog() {
        showDeleteDialog = false
        countDownFinished = false
    }

    suspend fun <T> handleReportsScreenState(response: NetworkResult<T>, action: () -> Unit = {}) {
        when (response) {
            is NetworkResult.Success -> {
                action()
                _uiState.update { it.copy(reportScreen = ReportsScreenUiState.Success) }
            }

            is NetworkResult.Error -> {
                action()
                _uiState.update { it.copy(reportScreen = ReportsScreenUiState.Failed(response.message)) }
                delay(3.seconds)
                _uiState.update { it.copy(reportScreen = ReportsScreenUiState.Initial) }
            }
        }
    }

    fun getAllProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(projects = ReportProjectsUiState.Loading) }
            when (val response = ProjectApi.getAllProjects(ProjectFilter(limit = -1))) {
                is NetworkResult.Error -> _uiState.update { it.copy(projects = ReportProjectsUiState.Failed(response.message)) }
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        projects = ReportProjectsUiState.Success(
                            response.data.data ?: emptyList()
                        )
                    )
                }
            }
        }
    }

    fun createReport() {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiState.update { it.copy(reportScreen = ReportsScreenUiState.Loading) }
            val response = ReportApi.createReport(ReportCreateReq(projectId = project!!.id, detail = detail))
            handleReportsScreenState(response, ::closeReportFormDialog)
        }
    }

    fun updateReport() {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiState.update { it.copy(reportScreen = ReportsScreenUiState.Loading) }
            val response = ReportApi.updateReport(currentReportId!!, ReportUpdateReq(detail = detail))
            handleReportsScreenState(response, ::closeReportFormDialog)
        }
    }

    fun deleteReport() {
        closeDeleteReportDialog()
        viewModelScope.launch {
            _uiState.update { it.copy(reportScreen = ReportsScreenUiState.Loading) }
            val response = ReportApi.deleteReport(currentReportId!!)
            handleReportsScreenState(response) { currentReportId = null }
        }
    }
}