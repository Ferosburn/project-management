package com.ferosburn.project_management.screen.contributors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val windowSize = LocalWindowSize.current

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
    }

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
        when (state) {
            is ContributorsUiState.Failed -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (state as ContributorsUiState.Failed).message,
                    )
                }
            }

            ContributorsUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is ContributorsUiState.Success -> {
                val users = (state as ContributorsUiState.Success).data
                if (users.isEmpty()) Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No Data",
                    )
                } else LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(users) { user ->
                        Row(
                            Modifier.clickable(onClick = { onUserClick(user.id) }),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(user.name, Modifier.weight(1f).align(Alignment.CenterVertically))
                            Text(if (user.isActive) "Active" else "Inactive")
                            if (viewModel.isSuperAdmin) {
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    Icons.Filled.Edit,
                                    null,
                                    Modifier.clip(CircleShape).clickable(onClick = { navigateToManageUser(user) })
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}