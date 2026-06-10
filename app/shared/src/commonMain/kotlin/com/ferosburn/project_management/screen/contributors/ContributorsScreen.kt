package com.ferosburn.project_management.screen.contributors

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.ferosburn.project_management.component.pagination.PaginatedLazyColumn
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize

@Composable
fun ContributorsScreen(
    onUserClick: (userId: String) -> Unit,
    navigateToManageUser: (user: UserRes?) -> Unit,
) {
    val viewModel = remember { ContributorsViewModel() }
    val lazyPagingItems = viewModel.getPagedUsers.collectAsLazyPagingItems()
    val windowSize = LocalWindowSize.current

    AppScaffold(
        title = "Contributors",
        showBackButton = false,
        sideContentOnTop = true,
        sideContent = {
            if (viewModel.isSuperAdmin) {
                Button(
                    onClick = { navigateToManageUser(null) },
                    Modifier.align(if (windowSize == WindowSize.EXPANDED) Alignment.Start else Alignment.End)
                        .padding(20.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Add Contributor", fontSize = 14.sp)
                }
            }
        },
    ) {
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
                if (viewModel.isSuperAdmin) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.Edit,
                        null,
                        Modifier.clip(CircleShape).clickable(onClick = { navigateToManageUser(user) }).padding(8.dp)
                    )
                }
            }
        }
    }
}