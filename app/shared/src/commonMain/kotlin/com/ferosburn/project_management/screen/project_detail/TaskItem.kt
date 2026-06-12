@file:OptIn(ExperimentalMaterial3Api::class)

package com.ferosburn.project_management.screen.project_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferosburn.project_management.models.task.TaskRes
import com.ferosburn.project_management.models.task.TaskStatus

@Composable
fun TaskItem(
    task: TaskRes,
    projectId: String,
    viewModel: ProjectDetailViewModel,
    state: ProjectDetailUiState,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf(task.status) }

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
                Button(
                    onClick = { if (viewModel.isManager) statusExpanded = !statusExpanded },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(status.label, fontSize = 14.sp)
                }
                DropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false },
                    modifier = Modifier.width(120.dp)
                ) {
                    TaskStatus.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.label, fontSize = 12.sp) },
                            onClick = {
                                viewModel.updateTaskStatus(projectId, task.id, item) { status = item }
                                statusExpanded = false
                            },
                        )
                    }
                }
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, contentDescription = "More options"
                    )
                }
                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("See Detail") }, onClick = {
                        expanded = false
                        viewModel.openTaskDetail(projectId, task.id)
                    })
                    if (viewModel.isManager) {
                        DropdownMenuItem(text = { Text("Create New Sub-task") }, onClick = {
                            expanded = false
                            viewModel.taskDelegate.openCreateTaskDialog(task.id, task.rootParentId ?: task.id)
                        })
                        DropdownMenuItem(text = { Text("Edit") }, onClick = {
                            expanded = false
                            viewModel.taskDelegate.openEditTaskDialog(task)
                        })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = {
                            expanded = false
                            viewModel.taskDelegate.openDeleteTaskDialog(task.id)
                        })
                    }
                }
            }
        }
        task.tasks.forEach {
            TaskItem(it, projectId, viewModel, state, Modifier.padding(start = 20.dp))
        }
    }
}