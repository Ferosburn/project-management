package com.ferosburn.project_management.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.project.ProjectFilter
import com.ferosburn.project_management.models.report.ReportCreateReq
import com.ferosburn.project_management.models.report.ReportUpdateReq
import com.ferosburn.project_management.network.*
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AccountScreenStates())
    val uiState = _uiState.asStateFlow()
    val reportsDelegate = AccountReportsDelegate()
    val userId = LocalStorage.readUserId()

    fun init() {
        getUserProfile()
        getContributedProjects()
        getUserReports()
    }

    suspend fun <T> handleOverlayState(response: NetworkResult<T>, action: () -> Unit = {}, isLogout: Boolean = false) {
        when (response) {
            is NetworkResult.Success -> {
                action()
                _uiState.update {
                    it.copy(
                        overlay = if (isLogout) AccountOverlayUiState.SuccessLogout else AccountOverlayUiState.Success
                    )
                }
            }

            is NetworkResult.Error -> {
                action()
                _uiState.update { it.copy(overlay = AccountOverlayUiState.Failed(response.message)) }
                delay(3.seconds)
                _uiState.update { it.copy(overlay = AccountOverlayUiState.Initial) }
            }
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(profile = AccountDataUiState.Loading) }
            when (val response = ProfileApi.getUserProfile()) {
                is NetworkResult.Error -> _uiState.update { it.copy(profile = AccountDataUiState.Failed(response.message)) }
                is NetworkResult.Success -> response.data.data?.let { data ->
                    _uiState.update { it.copy(profile = AccountDataUiState.Success(data)) }
                }
            }
        }
    }

    fun getContributedProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(projects = AccountProjectsUiState.Loading) }
            when (val response = ProfileApi.getContributedProjects()) {
                is NetworkResult.Error -> _uiState.update { it.copy(projects = AccountProjectsUiState.Failed(response.message)) }
                is NetworkResult.Success -> response.data.data?.let { data ->
                    _uiState.update { it.copy(projects = AccountProjectsUiState.Success(data)) }
                }
            }
        }
    }

    fun getAllProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(allProjects = AccountAllProjectsUiState.Loading) }
            when (val response = ProjectApi.getAllProjects(ProjectFilter(limit = -1))) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(allProjects = AccountAllProjectsUiState.Failed(response.message))
                }

                is NetworkResult.Success -> _uiState.update {
                    it.copy(allProjects = AccountAllProjectsUiState.Success(response.data.data ?: emptyList()))
                }
            }
        }
    }

    fun getUserReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(reports = AccountReportsUiState.Loading) }
            when (val response = ProfileApi.getUserReports()) {
                is NetworkResult.Error -> _uiState.update { it.copy(reports = AccountReportsUiState.Failed(response.message)) }
                is NetworkResult.Success -> _uiState.update {
                    it.copy(reports = AccountReportsUiState.Success(response.data.data ?: emptyList()))
                }
            }
        }
    }

    fun createUserReport() {
        if (!reportsDelegate.validateForm()) return
        viewModelScope.launch {
            _uiState.update { it.copy(overlay = AccountOverlayUiState.Loading) }
            val response = ReportApi.createReport(
                ReportCreateReq(
                    projectId = reportsDelegate.state.value.project!!.id, detail = reportsDelegate.state.value.detail
                )
            )
            handleOverlayState(response, reportsDelegate::closeFormDialog)
        }
    }

    fun updateUserReport() {
        if (!reportsDelegate.validateForm()) return
        viewModelScope.launch {
            _uiState.update { it.copy(overlay = AccountOverlayUiState.Loading) }
            val response = ReportApi.updateReport(
                reportsDelegate.state.value.currentReportId!!,
                ReportUpdateReq(detail = reportsDelegate.state.value.detail),
            )
            handleOverlayState(response, reportsDelegate::closeFormDialog)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(overlay = AccountOverlayUiState.Loading) }
            val response = AuthApi.logout()
            handleOverlayState(response, { LocalStorage.deleteTokenRes() })
        }
    }
}