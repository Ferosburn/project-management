package com.ferosburn.project_management.screen.project_detail

import com.ferosburn.project_management.models.report.ReportRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectReportDelegate {
    private val _state = MutableStateFlow(ProjectReportManageStates())
    val state = _state.asStateFlow()

    fun onDetailChange(value: String) = _state.update { it.copy(detail = value, detailError = null) }

    fun openReportFormDialog(report: ReportRes? = null) = _state.update {
        it.copy(
            isEdit = report != null,
            showFormDialog = true,
            detail = report?.detail ?: "",
            currentReportId = report?.id,
        )
    }

    fun closeReportFormDialog() = _state.update {
        it.copy(
            detail = "",
            detailError = null,
            currentReportId = null,
            showFormDialog = false,
        )
    }

    fun openDeleteReportDialog(reportId: String) = _state.update {
        it.copy(
            currentReportId = reportId,
            showDeleteDialog = true,
        )
    }

    fun closeDeleteReportDialog(clearCurrentReportId: Boolean = false) {
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
                }
            )
        }
        return listOf(_state.value.detailError).all { it.isNullOrBlank() }
    }
}