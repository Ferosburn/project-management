package com.ferosburn.project_management

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.ferosburn.project_management.navigation.NavHost
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.windowSize

@Composable
@Preview
fun App() {
    CompositionLocalProvider(
        LocalWindowSize provides windowSize(),
    ) {
        NavHost()
    }
}