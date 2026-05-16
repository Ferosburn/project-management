package com.ferosburn.project_management.screen.contributor_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.scaffold.AppScaffold

@Composable
fun ContributorDetailScreen(
    userId: String,
    onBack: () -> Unit,
) {
    val viewModel = remember { ContributorDetailViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getUserDetail(userId = userId)
    }

    AppScaffold(
        title = "Detail",
        onBack = onBack,
    ) {
        when (state) {
            ContributorDetailUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is ContributorDetailUiState.Success -> {
                val user = (state as ContributorDetailUiState.Success).data
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("id: ${user.id}")
                    Text("name: ${user.name}")
                    Text("username: ${user.username}")
                    Text("role: ${user.role.name}")
                    Text("division: ${user.division?.name ?: "-"}")
                    Text("isActive: ${user.isActive}")
                }
            }

            is ContributorDetailUiState.Failed -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (state as ContributorDetailUiState.Failed).message,
                    )
                }
            }
        }
    }
}