package com.ferosburn.project_management.screen.project_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferosburn.project_management.models.task.TaskCreateReq
import com.ferosburn.project_management.models.task.TaskRes
import com.ferosburn.project_management.models.task.TaskUpdateReq
import com.ferosburn.project_management.models.user.UserRole
import com.ferosburn.project_management.network.NetworkResult
import com.ferosburn.project_management.network.ProjectApi
import com.ferosburn.project_management.network.TaskApi
import com.ferosburn.project_management.storage.LocalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProjectDetailViewModel : ViewModel() {
    private val _uiStates = MutableStateFlow(ProjectDetailUiState())
    val uiStates = _uiStates.asStateFlow()
    val currentRole = LocalStorage.readUserRole()
    val isManager = currentRole != null && currentRole == UserRole.MANAGER
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)
    var descriptionError by mutableStateOf<String?>(null)
    var showCreateTaskDialog by mutableStateOf(false)
    var isEditTask by mutableStateOf(false)
    var currentTaskId by mutableStateOf<String?>(null)
    var currentParentId by mutableStateOf<String?>(null)
    var currentRootParentId by mutableStateOf<String?>(null)
    var showDeleteDialog by mutableStateOf(false)
    var tabExpanded by mutableStateOf(false)

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

    fun init(projectId: String, isExpanded: Boolean) {
        getProjectDetail(projectId = projectId)
        getAllTasks(projectId = projectId)
        updateTabs(setOf(ProjectDetailTab.REPORTS))
        if (isExpanded) {
            setSelectedTab(ProjectDetailTab.REPORTS)
        }
    }

    fun onNameChange(value: String) {
        name = value
        nameError = null
    }

    fun onDescriptionChange(value: String) {
        description = value
        descriptionError = null
    }

    fun openCreateTaskDialog(parentId: String, rootParentId: String) {
        isEditTask = false
        showCreateTaskDialog = true
        currentParentId = parentId
        currentRootParentId = rootParentId
    }

    fun openEditTaskDialog(task: TaskRes) {
        isEditTask = true
        showCreateTaskDialog = true
        name = task.name
        description = task.description
        currentTaskId = task.id
    }

    fun closeCreateTaskDialog() {
        name = ""
        nameError = null
        description = ""
        descriptionError = null
        showCreateTaskDialog = false
        currentTaskId = null
        currentParentId = null
        currentRootParentId = null
    }

    fun openDeleteTaskDialog(taskId: String) {
        showDeleteDialog = true
        currentTaskId = taskId
    }

    fun closeDeleteTaskDialog() {
        showDeleteDialog = false
    }

    fun openTaskDetail(taskId: String) {
        val tabs = _uiStates.value.tabs
        updateTabs(listOf(tabs, listOf(ProjectDetailTab.DETAIL)).flatten().toSet())
        setSelectedTab(ProjectDetailTab.DETAIL)
        tabExpanded = true
        currentTaskId = taskId
    }

    fun updateTabs(tabs: Set<ProjectDetailTab>) {
        _uiStates.update { it.copy(tabs = tabs) }
    }

    fun setSelectedTab(tab: ProjectDetailTab?) {
        _uiStates.update { it.copy(selectedTab = tab) }
    }

    suspend fun <T> handleManageTaskState(
        response: NetworkResult<T>, projectId: String, action: () -> Unit
    ) {
        when (response) {
            is NetworkResult.Success -> {
                action()
                _uiStates.update { it.copy(taskCreate = TaskCreateUiState.Success) }
                getAllTasks(projectId)
            }

            is NetworkResult.Error -> {
                action()
                _uiStates.update { it.copy(taskCreate = TaskCreateUiState.Failed(response.message)) }
                delay(3.seconds)
                _uiStates.update { it.copy(taskCreate = TaskCreateUiState.Initial) }
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

    fun getAllTasks(projectId: String) {
        viewModelScope.launch {
            _uiStates.update { it.copy(tasks = TasksUiState.Loading) }
            when (val response = TaskApi.getAllTasks(projectId)) {
                is NetworkResult.Error -> _uiStates.update { it.copy(tasks = TasksUiState.Failed(response.message)) }
                is NetworkResult.Success -> {
                    response.data.data?.let { data ->
                        _uiStates.update { it.copy(tasks = TasksUiState.Success(data)) }
                    }
                }
            }
        }
    }

    fun createTask(projectId: String) {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiStates.update { it.copy(taskCreate = TaskCreateUiState.Loading) }
            val response = TaskApi.createTask(
                projectId,
                TaskCreateReq(
                    name = name,
                    description = description,
                    parentId = currentParentId,
                    rootParentId = currentRootParentId
                ),
            )
            handleManageTaskState(response, projectId, ::closeCreateTaskDialog)
        }
    }

    fun updateTask(projectId: String) {
        if (!validateForm()) return
        viewModelScope.launch {
            _uiStates.update { it.copy(taskCreate = TaskCreateUiState.Loading) }
            val response = TaskApi.updateTask(
                projectId,
                currentTaskId!!,
                TaskUpdateReq(
                    name = name,
                    description = description,
                ),
            )
            handleManageTaskState(response, projectId, ::closeCreateTaskDialog)
        }
    }

    fun deleteTask(projectId: String) {
        closeDeleteTaskDialog()
        viewModelScope.launch {
            _uiStates.update { it.copy(taskCreate = TaskCreateUiState.Loading) }
            val response = TaskApi.deleteTask(
                projectId,
                currentTaskId!!,
            )
            handleManageTaskState(response, projectId) { currentTaskId = null }
        }
    }
}