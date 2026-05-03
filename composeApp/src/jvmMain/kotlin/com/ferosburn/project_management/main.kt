package com.ferosburn.project_management

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Project Management App",
    ) {
        App()
    }
}