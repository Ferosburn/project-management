package com.ferosburn.project_management.screen.projects

import androidx.lifecycle.ViewModel
import com.ferosburn.project_management.models.project.ProjectFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProjectsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectsUiState>(ProjectsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getAllProjects(filter: ProjectFilter = ProjectFilter()) {

    }
}