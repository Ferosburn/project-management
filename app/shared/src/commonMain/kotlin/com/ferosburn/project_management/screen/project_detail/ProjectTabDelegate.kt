package com.ferosburn.project_management.screen.project_detail

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectTabDelegate {
    private val _state = MutableStateFlow(ProjectTabManageStates())
    val state = _state.asStateFlow()

    fun updateTabs(tabs: Set<ProjectDetailTab>, activeTab: ProjectDetailTab?, expanded: Boolean = false) {
        _state.update { it.copy(tabs = tabs, activeTab = activeTab, expanded = expanded) }
    }

    fun setActiveTab(activeTab: ProjectDetailTab?) {
        _state.update { it.copy(activeTab = activeTab, expanded = activeTab != null) }
    }
}