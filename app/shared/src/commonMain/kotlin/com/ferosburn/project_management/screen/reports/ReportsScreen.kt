@file:OptIn(FormatStringsInDatetimeFormats::class, ExperimentalMaterial3Api::class)

package com.ferosburn.project_management.screen.reports

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.ferosburn.project_management.component.misc.SimpleCountDown
import com.ferosburn.project_management.component.pagination.PaginatedLazyColumn
import com.ferosburn.project_management.component.report.ReportItem
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.component.utils.clearFocusOnTap
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlin.time.Duration.Companion.seconds

@Composable
@Preview
fun ReportsScreen() {
    val viewModel = remember { ReportsViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.getPagedReports.collectAsLazyPagingItems()
    val windowSize = LocalWindowSize.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.getAllProjects()
    }

    LaunchedEffect(state.reportScreen) {
        if (state.reportScreen is ReportsScreenUiState.Success) {
            lazyPagingItems.refresh()
        }
    }


    Box(contentAlignment = Alignment.TopCenter) {
        AppScaffold(
            title = "Reports",
            showBackButton = false,
            sideContentOnTop = true,
            sideContent = {
                Button(
                    onClick = { viewModel.openCreateReportDialog() },
                    Modifier.align(if (windowSize == WindowSize.EXPANDED) Alignment.Start else Alignment.End)
                        .padding(20.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Create Report", fontSize = 14.sp)
                }
            },
        ) {
            PaginatedLazyColumn(lazyPagingItems, itemKey = { it.id }) { report ->
                ReportItem(
                    viewModel.userId,
                    report,
                    viewModel::openEditReportDialog,
                    viewModel::openDeleteReportDialog,
                )
            }
        }

        if (viewModel.showReportFormDialog) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.padding(20.dp).widthIn(max = 400.dp).fillMaxWidth()
                    .shadow(10.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(20.dp).clearFocusOnTap(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!viewModel.isEditReport) ExposedDropdownMenuBox(
                    expanded = viewModel.projectDropdownExpanded,
                    onExpandedChange = { viewModel.onProjectExpandChange() },
                ) {
                    OutlinedTextField(
                        value = viewModel.project?.name ?: "",
                        onValueChange = {},
                        label = { Text("Project", fontSize = 12.sp) },
                        supportingText = {
                            if (!viewModel.projectError.isNullOrBlank()) Text(
                                text = viewModel.projectError!!, fontSize = 12.sp
                            )
                        },
                        isError = !viewModel.projectError.isNullOrBlank(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.projectDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        textStyle = TextStyle(fontSize = 12.sp),
                    )
                    ExposedDropdownMenu(
                        expanded = viewModel.projectDropdownExpanded,
                        onDismissRequest = {
                            viewModel.projectDropdownExpanded = false
                            focusManager.clearFocus()
                        },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Choose one", fontSize = 12.sp) },
                            onClick = {
                                viewModel.onProjectChange(null)
                                focusManager.clearFocus()
                            },
                        )
                        if (state.projects is ReportProjectsUiState.Success) {
                            (state.projects as ReportProjectsUiState.Success).data.forEach { project ->
                                DropdownMenuItem(
                                    text = { Text(project.name, fontSize = 12.sp) },
                                    onClick = {
                                        viewModel.onProjectChange(project)
                                        focusManager.clearFocus()
                                    },
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = viewModel.detail,
                    onValueChange = viewModel::onDetailChange,
                    label = { Text("Description", fontSize = 12.sp) },
                    supportingText = {
                        if (!viewModel.detailError.isNullOrBlank()) Text(
                            text = viewModel.detailError!!, fontSize = 12.sp
                        )
                    },
                    isError = !viewModel.detailError.isNullOrBlank(),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                Row {
                    Button(
                        onClick = {
                            if (viewModel.isEditReport) {
                                viewModel.updateReport()
                            } else {
                                viewModel.createReport()
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
                        onClick = viewModel::closeReportFormDialog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }
        }

        if (viewModel.showDeleteDialog) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable(
                onClick = viewModel::closeDeleteReportDialog
            ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.padding(20.dp).widthIn(max = 400.dp).fillMaxWidth()
                    .shadow(10.dp, shape = RoundedCornerShape(10.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Delete Report Permanently?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("You can not reverse this action!")
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Button(
                        onClick = {
                            viewModel.closeDeleteReportDialog()
                            viewModel.deleteReport()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = viewModel.countDownFinished,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray,
                        )
                    ) {
                        if (viewModel.countDownFinished) {
                            Text("Delete", fontSize = 14.sp)
                        } else {
                            SimpleCountDown(10.seconds, onFinish = { viewModel.countDownFinished = true })
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = viewModel::closeDeleteReportDialog,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }
        }

        if (state.reportScreen is ReportsScreenUiState.Loading) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        AnimatedVisibility(
            visible = state.reportScreen is ReportsScreenUiState.Failed,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            Box(
                Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
            ) {
                val errorMessage = (state.reportScreen as? ReportsScreenUiState.Failed)?.message ?: ""
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
    }
}