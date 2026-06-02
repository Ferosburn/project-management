package com.ferosburn.project_management.screen.project_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferosburn.project_management.models.report.ReportRes
import com.ferosburn.project_management.utils.WindowSize
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun ProjectDetailSideView(
    modifier: Modifier = Modifier,
    projectId: String,
    viewModel: ProjectDetailViewModel,
    tabManageState: ProjectTabManageStates,
    state: ProjectDetailUiState,
    windowSize: WindowSize,
) {
    fun Modifier.contentWidth(): Modifier {
        return when (windowSize) {
            WindowSize.COMPACT -> this.fillMaxWidth(0.75f)
            WindowSize.MEDIUM -> this.width(400.dp)
            WindowSize.EXPANDED -> this.width(400.dp)
        }
    }

    Row(modifier.fillMaxHeight()) {
        Row(
            Modifier.padding(vertical = 20.dp).align(Alignment.Bottom).graphicsLayer(rotationZ = 270f)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.height, placeable.width) {
                        placeable.placeRelative(
                            x = -(placeable.width / 2 - placeable.height / 2),
                            y = -(placeable.height / 2 - placeable.width / 2)
                        )
                    }
                }, horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabManageState.tabs.forEach { tab ->
                val shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                Text(
                    tab.label,
                    Modifier.shadow(if (tab == tabManageState.activeTab) 10.dp else 5.dp, shape)
                        .background(if (tab == tabManageState.activeTab) Color.White else Color.Gray, shape)
                        .clickable(enabled = !(tabManageState.activeTab == tab && windowSize == WindowSize.EXPANDED)) {
                            if (tabManageState.activeTab == tab) {
                                viewModel.tabDelegate.setActiveTab(null)
                            } else {
                                viewModel.tabDelegate.setActiveTab(tab)
                            }
                        }.padding(4.dp),
                    color = if (tab == tabManageState.activeTab) Color.Black else Color.White,
                )
            }
        }

        AnimatedVisibility(
            visible = windowSize == WindowSize.EXPANDED || tabManageState.expanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally(),
        ) {
            Column(Modifier.fillMaxHeight().contentWidth().background(Color.White)) {
                if (tabManageState.activeTab == ProjectDetailTab.DETAIL) {
                    Icon(
                        Icons.Filled.Cancel, null,
                        Modifier.align(Alignment.End).clickable {
                            viewModel.closeTaskDetail(windowSize == WindowSize.EXPANDED)
                        },
                    )
                }
                when (tabManageState.activeTab) {
                    ProjectDetailTab.REPORTS -> ReportsTabView(projectId, viewModel, state.reports)
                    ProjectDetailTab.DETAIL -> TaskDetailTabView(state.task)
                    null -> {}
                }
            }
        }
    }
}

@Composable
fun TaskDetailTabView(state: TaskDetailUiState) {
    when (state) {
        is TaskDetailUiState.Failed -> Box(
            Modifier.fillMaxSize().padding(20.dp),
            contentAlignment = Alignment.Center,
        ) { Text(text = state.message) }

        TaskDetailUiState.Loading -> Box(
            Modifier.fillMaxSize().padding(20.dp),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        is TaskDetailUiState.Success -> {
            val detail = state.data
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(detail.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("id: ${detail.id}", color = Color.Gray, fontSize = 10.sp, lineHeight = 10.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(detail.status.name, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(detail.description, textAlign = TextAlign.Justify)
            }
        }
    }
}

@Composable
fun ReportsTabView(
    projectId: String,
    viewModel: ProjectDetailViewModel,
    state: ProjectReportsUiState,
) {
    LaunchedEffect(Unit) { viewModel.getReports(projectId) }

    when (state) {
        is ProjectReportsUiState.Failed -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { Text(text = state.message) }

        ProjectReportsUiState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        is ProjectReportsUiState.Success -> {
            Button(
                onClick = { viewModel.reportDelegate.openReportFormDialog() },
                Modifier.padding(20.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Create Report", fontSize = 14.sp)
            }
            ReportsListView(
                state.data,
                viewModel.userId,
                viewModel.reportDelegate::openReportFormDialog,
                viewModel.reportDelegate::openDeleteReportDialog
            )
        }
    }
}

@Composable
fun ReportsListView(
    reports: List<ReportRes>,
    userId: String?,
    onOpenReportFormDialog: (report: ReportRes) -> Unit,
    onOpenDeleteReportDialog: (reportId: String) -> Unit,
) {
    if (reports.isEmpty()) Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) { Text(text = "No Data") } else LazyColumn(
        Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(20.dp)) }
        items(reports) { report ->
            Column(
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
                            "(Deactivated)", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(Modifier.weight(1f))
                    Text(
                        Instant.fromEpochMilliseconds(report.createdAt).toLocalDateTime(TimeZone.currentSystemDefault())
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
                    if (userId == report.user.id) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert, contentDescription = "More options"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Edit") }, onClick = {
                                    expanded = false
                                    onOpenReportFormDialog(report)
                                })
                                DropdownMenuItem(text = { Text("Delete") }, onClick = {
                                    expanded = false
                                    onOpenDeleteReportDialog(report.id)
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
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}