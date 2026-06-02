package com.ferosburn.project_management.screen.project_detail

import com.ferosburn.project_management.models.task.TaskRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectTaskDelegate {
    private val _state = MutableStateFlow(ProjectTaskManageStates())
    val state = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value, nameError = null) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value, descriptionError = null) }
    }

    fun openCreateTaskDialog(parentId: String? = null, rootParentId: String? = null) {
        _state.update {
            it.copy(
                isEdit = false,
                showFormDialog = true,
                currentParentId = parentId,
                currentRootParentId = rootParentId,
            )
        }
    }

    fun openEditTaskDialog(task: TaskRes) {
        _state.update {
            it.copy(
                isEdit = true,
                showFormDialog = true,
                currentTaskId = task.id,
                name = task.name,
                description = task.description,
            )
        }
    }

    fun closeTaskFormDialog() {
        _state.update {
            it.copy(
                name = "",
                nameError = null,
                description = "",
                descriptionError = null,
                showFormDialog = false,
                isEdit = false,
                currentTaskId = null,
                currentParentId = null,
                currentRootParentId = null,
            )
        }
    }

    fun openDeleteTaskDialog(taskId: String) {
        _state.update { it.copy(showDeleteDialog = true, currentTaskId = taskId) }
    }

    fun closeDeleteTaskDialog(clearCurrentTaskId: Boolean = false) {
        _state.update { it.copy(showDeleteDialog = false) }
        if (clearCurrentTaskId) updateCurrentTaskId(null)
    }

    fun updateCurrentTaskId(value: String?) {
        _state.update { it.copy(currentTaskId = value) }
    }

    fun validateForm(): Boolean {
        _state.update {
            it.copy(
                nameError = when {
                    _state.value.name.isBlank() -> "name is required"
                    else -> null
                }, descriptionError = when {
                    _state.value.description.isBlank() -> "description is required"
                    else -> null
                }
            )
        }
        return listOf(_state.value.nameError, _state.value.descriptionError).all { it.isNullOrBlank() }
    }
}