package com.ferosburn.project_management.screen.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize

@Composable
fun ProjectsScreen(
    onProjectClick: (projectId: String) -> Unit,
) {
    val viewModel = remember { ProjectsViewModel() }
    val state by viewModel.uiState.collectAsState()
    val windowSize = LocalWindowSize.current

    LaunchedEffect(Unit) {
        viewModel.getAllProjects()
    }

    AppScaffold(
        title = "Projects",
        showBackButton = false,
        sideContentOnTop = true,
        sideContent = {
            if (viewModel.isManager) {
                Button(
                    onClick = { },
                    Modifier.align(if (windowSize == WindowSize.EXPANDED) Alignment.Start else Alignment.End)
                        .padding(20.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Create New Project", fontSize = 14.sp)
                }
            }
        },
    ) {
        when (state) {
            is ProjectsUiState.Failed -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (state as ProjectsUiState.Failed).message,
                    )
                }
            }

            ProjectsUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProjectsUiState.Success -> {
                val projects = (state as ProjectsUiState.Success).data
                if (projects.isEmpty()) Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No Data",
                    )
                } else LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(projects) { project ->
                        Row(
                            Modifier.clickable(onClick = { onProjectClick(project.id) }),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(project.name, Modifier.weight(1f).align(Alignment.CenterVertically))
                            Text(if (project.isActive) "Active" else "Inactive")
                            if (viewModel.isManager) {
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    Icons.Filled.Edit,
                                    null,
                                    Modifier.clip(CircleShape).clickable(onClick = { }).padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}