package com.ferosburn.project_management.screen.project_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.misc.SimpleCountDown
import com.ferosburn.project_management.component.overlays.BaseConfirmationDialog
import com.ferosburn.project_management.component.overlays.BaseDialogOverlay
import com.ferosburn.project_management.component.overlays.ErrorPopUp
import com.ferosburn.project_management.component.overlays.LoadingOverlay
import com.ferosburn.project_management.component.scaffold.AppScaffold2
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize
import kotlin.time.Duration.Companion.seconds

@Composable
fun ProjectDetailScreen(
    projectId: String,
    onBack: () -> Unit,
) {
    val viewModel = remember { ProjectDetailViewModel() }
    val state by viewModel.uiStates.collectAsStateWithLifecycle()
    val taskManageState by viewModel.taskDelegate.state.collectAsStateWithLifecycle()
    val tabManageState by viewModel.tabDelegate.state.collectAsStateWithLifecycle()
    val reportManageState by viewModel.reportDelegate.state.collectAsStateWithLifecycle()
    val windowSize = LocalWindowSize.current

    LaunchedEffect(Unit) {
        viewModel.init(projectId, windowSize == WindowSize.EXPANDED)
    }

    LaunchedEffect(windowSize) {
        viewModel.tabDelegate.setActiveTab(
            activeTab = if (tabManageState.activeTab == null && windowSize == WindowSize.EXPANDED) ProjectDetailTab.REPORTS else null
        )
    }

    AppScaffold2(
        title = "Project Detail", onBack = onBack,
        overlays = {
            if (windowSize == WindowSize.MEDIUM) {
                ProjectDetailSideView(
                    Modifier.align(Alignment.TopEnd),
                    projectId,
                    viewModel,
                    tabManageState,
                    state,
                    windowSize
                )
            }

            if (reportManageState.showFormDialog) BaseDialogOverlay(
                onClose = viewModel.reportDelegate::closeReportFormDialog
            ) {
                OutlinedTextField(
                    value = reportManageState.detail,
                    onValueChange = viewModel.reportDelegate::onDetailChange,
                    label = { Text("Detail", fontSize = 12.sp) },
                    supportingText = {
                        if (!reportManageState.detailError.isNullOrBlank()) Text(
                            text = reportManageState.detailError!!, fontSize = 12.sp
                        )
                    },
                    isError = !reportManageState.detailError.isNullOrBlank(),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                Row {
                    Button(
                        onClick = {
                            if (reportManageState.isEdit) {
                                viewModel.updateReport()
                            } else {
                                viewModel.createReport(projectId)
                            }
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
                        onClick = viewModel.reportDelegate::closeReportFormDialog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }

            if (taskManageState.showFormDialog) BaseDialogOverlay(
                onClose = viewModel.taskDelegate::closeTaskFormDialog
            ) {
                OutlinedTextField(
                    value = taskManageState.name,
                    onValueChange = viewModel.taskDelegate::onNameChange,
                    label = { Text("Name", fontSize = 12.sp) },
                    supportingText = {
                        if (!taskManageState.nameError.isNullOrBlank()) Text(
                            text = taskManageState.nameError!!, fontSize = 12.sp
                        )
                    },
                    isError = !taskManageState.nameError.isNullOrBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                OutlinedTextField(
                    value = taskManageState.description,
                    onValueChange = viewModel.taskDelegate::onDescriptionChange,
                    label = { Text("Description", fontSize = 12.sp) },
                    supportingText = {
                        if (!taskManageState.descriptionError.isNullOrBlank()) Text(
                            text = taskManageState.descriptionError!!, fontSize = 12.sp
                        )
                    },
                    isError = !taskManageState.descriptionError.isNullOrBlank(),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                Row {
                    Button(
                        onClick = {
                            if (taskManageState.isEdit) {
                                viewModel.updateTask(projectId)
                            } else {
                                viewModel.createTask(projectId)
                            }
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
                        onClick = viewModel.taskDelegate::closeTaskFormDialog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }

            if (reportManageState.showDeleteDialog) DeleteConfirmationDialog(
                title = "Delete Report Permanently?",
                description = "You can not reverse this action!",
                onClose = { viewModel.reportDelegate.closeDeleteReportDialog(true) },
                onDelete = viewModel::deleteReport
            )

            if (taskManageState.showDeleteDialog) DeleteConfirmationDialog(
                title = "Delete Task Permanently?",
                description = "You can not reverse this action!",
                onClose = { viewModel.taskDelegate.closeDeleteTaskDialog(true) },
                onDelete = { viewModel.deleteTask(projectId) }
            )

            ErrorPopUp(
                visible = state.overlay is ProjectDetailOverlayUiState.Failed,
                message = (state.overlay as? ProjectDetailOverlayUiState.Failed)?.message ?: ""
            )

            if (state.overlay is ProjectDetailOverlayUiState.Loading) LoadingOverlay()
        },
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
                when (windowSize) {
                    WindowSize.EXPANDED -> Row {
                        MainContent(projectId, state, viewModel, Modifier.weight(1f))
                        ProjectDetailSideView(
                            Modifier,
                            projectId,
                            viewModel,
                            tabManageState,
                            state,
                            windowSize
                        )
                    }

                    WindowSize.MEDIUM -> MainContent(projectId, state, viewModel)

                    WindowSize.COMPACT -> Box {
                        MainContent(projectId, state, viewModel)
                        ProjectDetailSideView(
                            Modifier.align(Alignment.TopEnd),
                            projectId,
                            viewModel,
                            tabManageState,
                            state,
                            windowSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    description: String,
    onClose: () -> Unit,
    onDelete: () -> Unit,
) {
    BaseConfirmationDialog(
        title = title,
        description = description,
        onClose = onClose,
    ) {
        var countDownFinished by remember { mutableStateOf(false) }

        Row {
            Button(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                enabled = countDownFinished,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray,
                )
            ) {
                if (countDownFinished) {
                    Text("Delete", fontSize = 14.sp)
                } else {
                    SimpleCountDown(10.seconds, onFinish = { countDownFinished = true })
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = onClose,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Cancel", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun MainContent(
    projectId: String,
    state: ProjectDetailUiState,
    viewModel: ProjectDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val project = (state.projectData as ProjectDataUiState.Success).data
    LazyColumn(
        modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    Text(text = state.tasks.message)
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
                val tasks = state.tasks.data
                if (viewModel.isManager) item {
                    Button(
                        onClick = { viewModel.taskDelegate.openCreateTaskDialog() },
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
                        TaskItem(task, projectId, viewModel, state)
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}