@file:OptIn(ExperimentalMaterial3Api::class)

package com.ferosburn.project_management.screen.account

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.overlays.BaseDialogOverlay
import com.ferosburn.project_management.component.report.ReportItem
import com.ferosburn.project_management.component.scaffold.AppScaffold2
import com.ferosburn.project_management.component.tooltip.InfoTooltipBox

@Composable
fun AccountScreen(
    onLogoutSuccess: () -> Unit,
) {
    val viewModel = remember { AccountViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editExpanded by remember { mutableStateOf(false) }
    val reportManageState by viewModel.reportsDelegate.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { viewModel.init() }

    LaunchedEffect(state.overlay) {
        when (state.overlay) {
            AccountOverlayUiState.Success -> viewModel.init()
            AccountOverlayUiState.SuccessLogout -> onLogoutSuccess()
            else -> Unit
        }
    }

    AppScaffold2(
        title = "Account", showBackButton = false,
        overlays = {
            AnimatedVisibility(
                visible = state.overlay is AccountOverlayUiState.Failed,
                enter = slideInVertically(),
                exit = slideOutVertically(),
            ) {
                Box(
                    Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                        .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
                ) {
                    val errorMessage = (state.overlay as? AccountOverlayUiState.Failed)?.message ?: ""
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

            if (state.overlay is AccountOverlayUiState.Loading) Box(
                Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            if (reportManageState.showFormDialog) BaseDialogOverlay(
                onClose = viewModel.reportsDelegate::closeFormDialog
            ) {
                LaunchedEffect(Unit) {
                    viewModel.getAllProjects()
                }

                if (!reportManageState.isEdit) ExposedDropdownMenuBox(
                    expanded = reportManageState.projectDropdownExpanded,
                    onExpandedChange = { viewModel.reportsDelegate.onProjectExpandChange() },
                ) {
                    OutlinedTextField(
                        value = reportManageState.project?.name ?: "",
                        onValueChange = {},
                        label = { Text("Project", fontSize = 12.sp) },
                        supportingText = {
                            if (!reportManageState.projectError.isNullOrBlank()) Text(
                                text = reportManageState.projectError!!, fontSize = 12.sp
                            )
                        },
                        isError = !reportManageState.projectError.isNullOrBlank(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reportManageState.projectDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        textStyle = TextStyle(fontSize = 12.sp),
                    )
                    ExposedDropdownMenu(
                        expanded = reportManageState.projectDropdownExpanded,
                        onDismissRequest = {
                            viewModel.reportsDelegate.onProjectExpandChange(false)
                            focusManager.clearFocus()
                        },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Choose one", fontSize = 12.sp) },
                            onClick = {
                                viewModel.reportsDelegate.onProjectChange(null)
                                focusManager.clearFocus()
                            },
                        )
                        when (val projectListState = state.allProjects) {
                            is AccountAllProjectsUiState.Success -> {
                                val projects = projectListState.data
                                projects.forEach { project ->
                                    DropdownMenuItem(
                                        text = { Text(project.name, fontSize = 12.sp) },
                                        onClick = {
                                            viewModel.reportsDelegate.onProjectChange(project)
                                            focusManager.clearFocus()
                                        },
                                    )
                                }
                            }

                            else -> Unit
                        }
                    }
                }
                OutlinedTextField(
                    value = reportManageState.detail,
                    onValueChange = viewModel.reportsDelegate::onDetailChange,
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
                                viewModel.updateUserReport()
                            } else {
                                viewModel.createUserReport()
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
                        onClick = viewModel.reportsDelegate::closeFormDialog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }
        },
    ) {
        when (val profileState = state.profile) {
            is AccountDataUiState.Failed -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text(text = profileState.message) }

            AccountDataUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is AccountDataUiState.Success -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(20.dp),
            ) {
                val profileData = profileState.data
                item {
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text(
                                profileData.name,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                profileData.division?.name ?: profileData.role.name,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box {
                            IconButton(
                                onClick = { editExpanded = !editExpanded },
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                InfoTooltipBox("Edit Profile") {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                            DropdownMenu(
                                expanded = editExpanded,
                                onDismissRequest = { editExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Profile", fontSize = 12.sp) },
                                    onClick = {
                                        editExpanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Change Password", fontSize = 12.sp) },
                                    onClick = {
                                        editExpanded = false
                                    },
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.logout() },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            InfoTooltipBox("Logout") {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                            }
                        }
                    }
                }
                when (val projectsState = state.projects) {
                    is AccountProjectsUiState.Failed -> {}
                    AccountProjectsUiState.Loading -> item {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator() }
                    }

                    is AccountProjectsUiState.Success -> {
                        val projects = projectsState.data
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "Contributed Projects",
                                    fontWeight = FontWeight.Bold,
                                )
                                projects.forEach { project ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Circle, contentDescription = null, Modifier.size(8.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(project.name, Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
                when (val reportsState = state.reports) {
                    is AccountReportsUiState.Failed -> {}
                    AccountReportsUiState.Loading -> item {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator() }
                    }

                    is AccountReportsUiState.Success -> {
                        val reports = reportsState.data
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Recent Reports",
                                        Modifier.weight(1f),
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Button(
                                        onClick = { viewModel.reportsDelegate.openFormDialog() },
                                        shape = RoundedCornerShape(12.dp),
                                    ) {
                                        Text("Create Report", fontSize = 14.sp)
                                    }
                                }
                                reports.forEach { report ->
                                    ReportItem(
                                        userId = viewModel.userId,
                                        report = report,
                                        onOpenEditDialog = viewModel.reportsDelegate::openFormDialog,
                                        onOpenDeleteDialog = viewModel.reportsDelegate::openDeleteDialog,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}