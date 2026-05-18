package com.ferosburn.project_management.screen.project_manage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.models.project.ProjectUpdateReq
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProjectManageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectManageUiState>(ProjectManageUiState.Initial)
    val uiState = _uiState.asStateFlow()
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)
    var descriptionError by mutableStateOf<String?>(null)
    var showDeleteDialog by mutableStateOf(false)
    var countDownFinished by mutableStateOf(false)

    fun fillForm(project: ProjectRes) {
        name = project.name
        description = project.description
    }

    private fun validateForm(): Boolean {
        nameError = when {
            name.isBlank() -> "name is required"
            else -> null
        }
        descriptionError = when {
            description.isBlank() -> "description is required"
            else -> null
        }
        return listOf(nameError, descriptionError).all { it.isNullOrBlank() }
    }

    fun onNameChange(value: String) {
        name = value
        nameError = null
    }

    fun onDescriptionChange(value: String) {
        description = value
        descriptionError = null
    }

    fun createProject() {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiState.value = ProjectManageUiState.Loading
            val response = ProjectApi.createProject(
                ProjectCreateReq(
                    name = name,
                    description = description,
                )
            )
            when (response) {
                is NetworkResult.Success -> _uiState.value = ProjectManageUiState.Success
                is NetworkResult.Error -> {
                    _uiState.value = ProjectManageUiState.Failed(response.message)
                    delay(3.seconds)
                    _uiState.value = ProjectManageUiState.Initial
                }
            }
        }
    }

    fun updateProject(projectId: String) {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiState.value = ProjectManageUiState.Loading
            val response = ProjectApi.updateProject(
                projectId,
                ProjectUpdateReq(
                    name = name,
                    description = description,
                ),
            )
            when (response) {
                is NetworkResult.Success -> _uiState.value = ProjectManageUiState.Success
                is NetworkResult.Error -> {
                    _uiState.value = ProjectManageUiState.Failed(response.message)
                    delay(3.seconds)
                    _uiState.value = ProjectManageUiState.Initial
                }
            }
        }
    }

    fun updateStatus(projectId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            _uiState.value = ProjectManageUiState.Loading
            val response = ProjectApi.updateProject(
                projectId = projectId,
                data = ProjectUpdateReq(
                    isActive = !currentStatus,
                ),
            )
            when (response) {
                is NetworkResult.Success -> _uiState.value = ProjectManageUiState.Success
                is NetworkResult.Error -> {
                    _uiState.value = ProjectManageUiState.Failed(response.message)
                    delay(3.seconds)
                    _uiState.value = ProjectManageUiState.Initial
                }
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            _uiState.value = ProjectManageUiState.Loading
            when (val response = ProjectApi.deleteProject(projectId)) {
                is NetworkResult.Success -> _uiState.value = ProjectManageUiState.Success
                is NetworkResult.Error -> {
                    _uiState.value = ProjectManageUiState.Failed(response.message)
                    delay(3.seconds)
                    _uiState.value = ProjectManageUiState.Initial
                }
            }
        }
    }
}