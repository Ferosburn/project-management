package com.ferosburn.project_management.screen.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AccountScreen(
    onLogoutSuccess: () -> Unit,
) {
    val viewModel = remember { AccountViewModel() }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Account Screen",
            Modifier.clickable(onClick = { viewModel.checkToken() }),
        )
    }
}