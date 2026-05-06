package com.ferosburn.project_management.component.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class MainNavigationItem<T : Any>(
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector = icon,
    val route: T,
)
