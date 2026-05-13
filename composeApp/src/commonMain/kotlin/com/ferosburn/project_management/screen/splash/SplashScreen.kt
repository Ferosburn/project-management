package com.ferosburn.project_management.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.navigation.NavChannel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen() {
    val viewModel = remember { SplashViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is SplashUiState.Success) {
            NavChannel.navigateToDashboard()
        } else if (state is SplashUiState.Failed) {
            NavChannel.navigateToLogin()
        }
    }

    LaunchedEffect(Unit) {
        delay(3.seconds)
        viewModel.checkAuth()
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Splash Screen",
        )
    }
}