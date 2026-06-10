package com.ferosburn.project_management.screen.project_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.ferosburn.project_management.component.pagination.PaginatedLazyColumn
import com.ferosburn.project_management.component.report.ReportItem
import com.ferosburn.project_management.utils.WindowSize

@Composable
fun ProjectDetailSideView(
    modifier: Modifier = Modifier,
    projectId: String,
    viewModel: ProjectDetailViewModel,
    tabManageState: ProjectTabManageStates,
    state: ProjectDetailUiState,
    windowSize: WindowSize,
    onUserClick: (userId: String) -> Unit
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
                    ProjectDetailTab.REPORTS -> ReportsTabView(projectId, viewModel)
                    ProjectDetailTab.DETAIL -> TaskDetailTabView(state.task)
                    ProjectDetailTab.CONTRIBUTORS -> ContributorsTabView(projectId, viewModel, onUserClick)
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
) {
    val pagedReportFlow = remember { viewModel.getPagedReports(projectId) }
    val lazyPagingItems = pagedReportFlow.collectAsLazyPagingItems()

    PaginatedLazyColumn(
        lazyPagingItems, itemKey = { it.id },
        header = {
            Button(
                onClick = { viewModel.reportDelegate.openReportFormDialog() },
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Create Report", fontSize = 14.sp)
            }
        },
    ) { report ->
        ReportItem(
            viewModel.userId,
            report,
            viewModel.reportDelegate::openReportFormDialog,
            viewModel.reportDelegate::openDeleteReportDialog
        )
    }
}

@Composable
fun ContributorsTabView(
    projectId: String,
    viewModel: ProjectDetailViewModel,
    onUserClick: (userId: String) -> Unit,
) {
    val pagedContributorFlow = remember { viewModel.getPagedContributors(projectId) }
    val lazyPagingItems = pagedContributorFlow.collectAsLazyPagingItems()

    PaginatedLazyColumn(lazyPagingItems, itemKey = { it.id }) { user ->
        Row(
            Modifier.clickable(onClick = { onUserClick(user.id) }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(user.name)
            Spacer(Modifier.weight(1f))
            Text(
                user.division?.name ?: user.role.name,
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (user.isActive) "Active" else "Inactive")
        }
    }
}
