package com.ferosburn.project_management.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT, MEDIUM, EXPANDED
}

val LocalWindowSize = compositionLocalOf<WindowSize> {
    error("LocalWindowSize not set")
}

@Composable
fun windowSize(): WindowSize {
    val containerSize = LocalWindowInfo.current.containerDpSize
    val width = containerSize.width
    val height = containerSize.height
    return when {
        width > 1200.dp && height >= 600.dp -> WindowSize.EXPANDED
        width > 600.dp && height >= 480.dp -> WindowSize.MEDIUM
        else -> WindowSize.COMPACT
    }
}