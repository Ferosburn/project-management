package com.ferosburn.project_management.screen.project_detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.component.utils.clearFocusOnTap
import com.ferosburn.project_management.models.task.TaskRes

@Composable
fun ProjectDetailScreen(
    projectId: String,
    onBack: () -> Unit,
) {
    val viewModel = remember { ProjectDetailViewModel() }
    val state by viewModel.uiStates.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getProjectDetail(projectId = projectId)
        viewModel.getAllTasks(projectId = projectId)
    }

    Box(
        contentAlignment = Alignment.TopCenter,
    ) {
        AppScaffold(
            title = "Project Detail",
            onBack = onBack,
        ) {
            when (state.projectData) {
                is ProjectDataUiState.Failed -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (state.projectData as ProjectDataUiState.Failed).message,
                    )
                }

                ProjectDataUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is ProjectDataUiState.Success -> {
                    val project = (state.projectData as ProjectDataUiState.Success).data
                    LazyColumn(
                        Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(Modifier.height(20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(project.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("id: ${project.id}", color = Color.Gray, fontSize = 10.sp, lineHeight = 10.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    if (project.isActive) "Active" else "Inactive", color = Color.Gray, fontSize = 14.sp
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(project.description, textAlign = TextAlign.Justify)
                            Spacer(Modifier.height(20.dp))
                        }
                        when (state.tasks) {
                            is TasksUiState.Failed -> item {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = (state.tasks as TasksUiState.Failed).message)
                                }
                            }

                            TasksUiState.Loading -> item {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is TasksUiState.Success -> {
                                val tasks = (state.tasks as TasksUiState.Success).data
                                if (viewModel.isManager) item {
                                    Button(
                                        onClick = { viewModel.showCreateTaskDialog = true },
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text("Create New Task", fontSize = 14.sp)
                                    }
                                }
                                if (tasks.isEmpty()) item {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) { Text("No Data") }
                                } else {
                                    item { Spacer(Modifier.height(8.dp)) }
                                    items(tasks) { task ->
                                        TaskItem(task, viewModel)
                                    }
                                    item { Spacer(Modifier.height(20.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.taskCreate is TaskCreateUiState.Failed,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            Box(
                Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
            ) {
                val errorMessage = (state.taskCreate as? TaskCreateUiState.Failed)?.message ?: ""
                AnimatedVisibility(
                    visible = errorMessage.isNotBlank(),
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally(),
                ) {
                    Text(
                        errorMessage, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (viewModel.showCreateTaskDialog) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.padding(20.dp).widthIn(max = 400.dp).fillMaxWidth()
                    .shadow(10.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(20.dp).clearFocusOnTap(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Name", fontSize = 12.sp) },
                    supportingText = {
                        if (!viewModel.nameError.isNullOrBlank()) Text(
                            text = viewModel.nameError!!, fontSize = 12.sp
                        )
                    },
                    isError = !viewModel.nameError.isNullOrBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Description", fontSize = 12.sp) },
                    supportingText = {
                        if (!viewModel.descriptionError.isNullOrBlank()) Text(
                            text = viewModel.descriptionError!!, fontSize = 12.sp
                        )
                    },
                    isError = !viewModel.descriptionError.isNullOrBlank(),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                Row {
                    Button(
                        onClick = {
                            viewModel.createTask(projectId)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray,
                        )
                    ) {
                        Text("Save", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = viewModel::closeCreateTaskDialog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }
        }

        if (state.taskCreate is TaskCreateUiState.Loading) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun TaskItem(task: TaskRes, viewModel: ProjectDetailViewModel, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier.shadow(10.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(task.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(task.description, fontSize = 10.sp, color = Color.Gray)
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Create New Sub-task") },
                        onClick = {
                            expanded = false
                            viewModel.openCreateTaskDialog(task.id, task.rootParentId ?: task.id)
                        }
                    )
                }
            }
        }
        task.tasks.forEach {
            TaskItem(it, viewModel, Modifier.padding(start = 20.dp))
        }
    }
}