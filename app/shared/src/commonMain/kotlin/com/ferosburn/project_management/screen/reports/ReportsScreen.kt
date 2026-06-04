@file:OptIn(FormatStringsInDatetimeFormats::class, ExperimentalMaterial3Api::class)

package com.ferosburn.project_management.screen.reports

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ferosburn.project_management.component.misc.SimpleCountDown
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.component.utils.clearFocusOnTap
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

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
            when (val refreshState = lazyPagingItems.loadState.refresh) {
                is LoadState.Error -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = refreshState.error.message ?: "An unexpected error occurred.")
                }

                LoadState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is LoadState.NotLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.id }) { index ->
                            val report = lazyPagingItems[index]
                            if (report != null) Column(
                                Modifier.shadow(10.dp, shape = RoundedCornerShape(12.dp))
                                    .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                var expanded by remember { mutableStateOf(false) }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        report.user.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!report.user.isActive) Color.Gray else Color.Black
                                    )
                                    if (!report.user.isActive) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "(Deactivated)",
                                            fontSize = 10.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        Instant.fromEpochMilliseconds(report.createdAt)
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                            .format(LocalDateTime.Format {
                                                hour(); char(':'); minute(); char(' ')
                                                dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
                                                chars(", "); day(); chars(" ")
                                                monthName(names = MonthNames.ENGLISH_ABBREVIATED)
                                                chars(" "); year()
                                            }),
                                        fontSize = 10.sp, color = Color.Gray,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (viewModel.userId == report.user.id) {
                                        Box {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "More options"
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                                DropdownMenuItem(text = { Text("Edit") }, onClick = {
                                                    expanded = false
                                                    viewModel.openEditReportDialog(report)
                                                })
                                                DropdownMenuItem(text = { Text("Delete") }, onClick = {
                                                    expanded = false
                                                    viewModel.openDeleteReportDialog(report.id)
                                                })
                                            }
                                        }
                                    }
                                }
                                Text(
                                    report.user.division?.name ?: report.user.role.name,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(report.project.name, fontSize = 14.sp)
                                Text(report.detail)
                            } else CircularProgressIndicator()
                        }
                        when (val appendState = lazyPagingItems.loadState.append) {
                            is LoadState.Error -> item {
                                Box(
                                    Modifier.fillMaxWidth().padding(20.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = appendState.error.message ?: "An unexpected error occurred.")
                                }
                            }

                            LoadState.Loading -> item {
                                Box(
                                    Modifier.fillMaxWidth().padding(20.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is LoadState.NotLoading -> {}
                        }
                    }
                }
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