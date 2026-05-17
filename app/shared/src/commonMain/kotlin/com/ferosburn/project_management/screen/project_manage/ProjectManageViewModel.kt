package com.ferosburn.project_management.screen.project_manage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.project.ProjectCreateReq
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProjectManageViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectManageUiState>(ProjectManageUiState.Initial)
    val uiState = _uiState.asStateFlow()
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)
    var descriptionError by mutableStateOf<String?>(null)

    fun onNameChange(value: String) {
        name = value
        nameError = null
    }

    fun onDescriptionChange(value: String) {
        description = value
        descriptionError = null
    }

    fun createProject() {
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
}