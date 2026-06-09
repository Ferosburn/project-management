package com.ferosburn.project_management.screen.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.ferosburn.project_management.component.pagination.PaginatedLazyColumn
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.models.project.ProjectRes
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize

@Composable
fun ProjectsScreen(
    onProjectClick: (projectId: String) -> Unit,
    navigateToManageProject: (project: ProjectRes?) -> Unit,
) {
    val viewModel = remember { ProjectsViewModel() }
    val lazyPagingItems = viewModel.getPagedProjects.collectAsLazyPagingItems()
    val windowSize = LocalWindowSize.current

    AppScaffold(
        title = "Projects",
        showBackButton = false,
        sideContentOnTop = true,
        sideContent = {
            if (viewModel.isManager) {
                Button(
                    onClick = { navigateToManageProject(null) },
                    Modifier.align(if (windowSize == WindowSize.EXPANDED) Alignment.Start else Alignment.End)
                        .padding(20.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Create New Project", fontSize = 14.sp)
                }
            }
        },
    ) {
        PaginatedLazyColumn(lazyPagingItems, itemKey = { it.id }) { project ->
            Row(
                Modifier.clickable(onClick = { onProjectClick(project.id) }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(project.name, Modifier.weight(1f).align(Alignment.CenterVertically))
                Text(if (project.isActive) "Active" else "Inactive")
                if (viewModel.isManager) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.Edit,
                        null,
                        Modifier.clip(CircleShape).clickable(onClick = { navigateToManageProject(project) })
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}