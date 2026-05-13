package com.ferosburn.project_management.screen.contributors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.models.user.UserRes

@Composable
fun ContributorsScreen(
    onUserClick: (user: UserRes) -> Unit,
) {
    val viewModel = remember { ContributorsViewModel() }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
    }

    AppScaffold(
        title = "Contributors",
        showBackButton = false,
    ) {
        when (state) {
            is ContributorsUiState.Failed -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (state as ContributorsUiState.Failed).message,
                    )
                }
            }

            ContributorsUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is ContributorsUiState.Success -> {
                val users = (state as ContributorsUiState.Success).data
                LazyColumn {
                    items(users) { user ->
                        Text(user.name, Modifier.clickable(onClick = { onUserClick(user) }))
                    }
                }
            }
        }
    }
}