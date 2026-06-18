package com.ferosburn.project_management.screen.account

import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.report.ReportRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AccountReportsDelegate() {
    private val _state = MutableStateFlow(AccountReportManageStates())
    val state = _state.asStateFlow()

    fun onDetailChange(value: String) = _state.update { it.copy(detail = value, detailError = null) }

    fun onProjectChange(value: ProjectRes?) = _state.update {
        it.copy(project = value, projectError = null, projectDropdownExpanded = false)
    }

    fun onProjectExpandChange(value: Boolean? = null) =
        _state.update { it.copy(projectDropdownExpanded = value ?: !it.projectDropdownExpanded) }

    fun openFormDialog(report: ReportRes? = null) = _state.update {
        it.copy(
            isEdit = report != null,
            showFormDialog = true,
            detail = report?.detail ?: "",
            project = report?.project,
            currentReportId = report?.id,
        )
    }

    fun closeFormDialog() = _state.update {
        it.copy(
            detail = "",
            detailError = null,
            project = null,
            projectError = null,
            projectDropdownExpanded = false,
            currentReportId = null,
            showFormDialog = false,
        )
    }

    fun openDeleteDialog(reportId: String) = _state.update {
        it.copy(currentReportId = reportId, showDeleteDialog = true)
    }

    fun closeDeleteDialog(clearCurrentReportId: Boolean = false) {
        _state.update { it.copy(showDeleteDialog = false) }
        if (clearCurrentReportId) updateCurrentReportId(null)
    }

    fun updateCurrentReportId(reportId: String?) = _state.update { it.copy(currentReportId = reportId) }

    fun validateForm(): Boolean {
        _state.update {
            it.copy(
                detailError = when {
                    _state.value.detail.isBlank() -> "detail is required"
                    else -> null
                },
                projectError = when {
                    !_state.value.isEdit && _state.value.project == null -> "project is required"
                    else -> null
                },
            )
        }
        return listOf(_state.value.detailError, _state.value.projectError).all { it.isNullOrBlank() }
    }
}