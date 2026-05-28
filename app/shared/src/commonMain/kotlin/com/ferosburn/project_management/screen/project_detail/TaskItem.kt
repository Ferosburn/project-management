package com.ferosburn.project_management.screen.project_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferosburn.project_management.models.task.TaskRes

@Composable
fun TaskItem(
    task: TaskRes, viewModel: ProjectDetailViewModel, state: ProjectDetailUiState, modifier: Modifier = Modifier
) {
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
                        imageVector = Icons.Default.MoreVert, contentDescription = "More options"
                    )
                }
                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("See Detail") }, onClick = {
                        expanded = false
                        viewModel.openTaskDetail(task.id)
                    })
                    DropdownMenuItem(text = { Text("Create New Sub-task") }, onClick = {
                        expanded = false
                        viewModel.openCreateTaskDialog(task.id, task.rootParentId ?: task.id)
                    })
                    DropdownMenuItem(text = { Text("Edit") }, onClick = {
                        expanded = false
                        viewModel.openEditTaskDialog(task)
                    })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = {
                        expanded = false
                        viewModel.openDeleteTaskDialog(task.id)
                    })
                }
            }
        }
        task.tasks.forEach {
            TaskItem(it, viewModel, state, Modifier.padding(start = 20.dp))
        }
    }
}