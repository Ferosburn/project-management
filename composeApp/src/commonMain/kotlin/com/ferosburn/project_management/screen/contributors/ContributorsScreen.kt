package com.ferosburn.project_management.screen.contributors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val state by viewModel.uiState.collectAsState()
    val windowSize = LocalWindowSize.current

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
    }

    AppScaffold(
        title = "Contributors",
        showBackButton = false,
        sideContentOnTop = true,
        sideContent = {
            Button(
                onClick = { navigateToManageUser(null) },
                Modifier.align(if (windowSize == WindowSize.EXPANDED) Alignment.Start else Alignment.End)
                    .padding(20.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Add Contributor", fontSize = 14.sp)
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
                LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(users) { user ->
                        Row(
                            Modifier.clickable(onClick = { onUserClick(user.id) }),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(user.name)
                            Spacer(Modifier.weight(1f))
                            IconButton(
                                onClick = { navigateToManageUser(user) },
                            ) {
                                Icon(Icons.Filled.Edit, null)
                            }
                        }
                    }
                }
            }
        }
    }
}