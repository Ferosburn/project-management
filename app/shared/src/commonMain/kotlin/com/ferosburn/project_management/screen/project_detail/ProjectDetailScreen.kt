package com.ferosburn.project_management.screen.project_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.scaffold.AppScaffold

@Composable
fun ProjectDetailScreen(
    projectId: String,
    onBack: () -> Unit,
) {
    val viewModel = remember { ProjectDetailViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getProjectDetail(projectId)
    }

    AppScaffold(
        title = "Project Detail",
        onBack = onBack,
    ) {
        when (state) {
            is ProjectDetailUiState.Failed -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (state as ProjectDetailUiState.Failed).message,
                )
            }

            ProjectDetailUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is ProjectDetailUiState.Success -> {
                val project = (state as ProjectDetailUiState.Success).data
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(project.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("id: ${project.id}", color = Color.Gray, fontSize = 10.sp)
                        }
                        Text(project.isActive.toString())
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(project.description, textAlign = TextAlign.Justify)
                }
            }
        }
    }
}