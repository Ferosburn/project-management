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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ferosburn.project_management.utils.WindowSize

@Composable
fun ProjectDetailSideView(
    modifier: Modifier = Modifier,
    viewModel: ProjectDetailViewModel,
    state: ProjectDetailUiState,
    windowSize: WindowSize,
) {
    fun Modifier.contentWidth(): Modifier {
        return when (windowSize) {
            WindowSize.COMPACT -> this.fillMaxWidth(0.75f)
            WindowSize.MEDIUM -> this.width(400.dp)
            WindowSize.EXPANDED -> this.fillMaxWidth()
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
            state.tabs.forEach { tab ->
                val shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                Text(
                    tab.label,
                    Modifier.shadow(if (tab == state.selectedTab) 10.dp else 5.dp, shape)
                        .background(if (tab == state.selectedTab) Color.White else Color.Gray, shape)
                        .clickable(enabled = !(state.selectedTab == tab && windowSize == WindowSize.EXPANDED)) {
                            if (state.selectedTab == tab) {
                                viewModel.setSelectedTab(null)
                                viewModel.tabExpanded = !viewModel.tabExpanded
                            } else {
                                viewModel.setSelectedTab(tab)
                                viewModel.tabExpanded = true
                            }
                        }.padding(4.dp),
                    color = if (tab == state.selectedTab) Color.Black else Color.White,
                )
            }
        }

        AnimatedVisibility(
            visible = windowSize == WindowSize.EXPANDED || viewModel.tabExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally(),
        ) {
            Column(Modifier.fillMaxHeight().contentWidth().background(Color.White).padding(20.dp)) {
                if (state.selectedTab == ProjectDetailTab.DETAIL) {
                    Icon(
                        Icons.Filled.Cancel, null,
                        Modifier.align(Alignment.End).clickable {
                            val filteredTabs = state.tabs.filter { tab -> tab != ProjectDetailTab.DETAIL }.toSet()
                            viewModel.updateTabs(filteredTabs)
                            if (windowSize == WindowSize.EXPANDED) {
                                viewModel.setSelectedTab(filteredTabs.last())
                            } else {
                                viewModel.setSelectedTab(null)
                            }
                            viewModel.currentTaskId = null
                            viewModel.tabExpanded = false
                        },
                    )
                }
                when (state.selectedTab) {
                    ProjectDetailTab.REPORTS -> {
                        Text(state.selectedTab.label)
                        Text("taskId: ${viewModel.currentTaskId ?: "-"}")
                    }

                    ProjectDetailTab.DETAIL -> TaskDetailView(state.task)
                    null -> {
                        Text(state.selectedTab?.label ?: "-")
                        Text("taskId: ${viewModel.currentTaskId ?: "-"}")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailView(state: TaskDetailUiState) {
    when (state) {
        is TaskDetailUiState.Failed -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = state.message)
        }

        TaskDetailUiState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        is TaskDetailUiState.Success -> {
            val detail = state.data
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(detail.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("id: ${detail.id}", color = Color.Gray, fontSize = 10.sp, lineHeight = 10.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        detail.status.name, color = Color.Gray, fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(detail.description, textAlign = TextAlign.Justify)
            }
        }
    }
}