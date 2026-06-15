package com.ferosburn.project_management.screen.account

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.scaffold.AppScaffold2
import com.ferosburn.project_management.component.tooltip.InfoTooltipBox

@Composable
fun AccountScreen(
    onLogoutSuccess: () -> Unit,
) {
    val viewModel = remember { AccountViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUserProfile()
    }

    LaunchedEffect(state.overlay) {
        if (state.overlay is AccountOverlayUiState.SuccessLogout) {
            onLogoutSuccess()
        }
    }

    AppScaffold2(
        title = "Account", showBackButton = false,
        overlays = {
            AnimatedVisibility(
                visible = state.overlay is AccountOverlayUiState.Failed,
                enter = slideInVertically(),
                exit = slideOutVertically(),
            ) {
                Box(
                    Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                        .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
                ) {
                    val errorMessage = (state.overlay as? AccountOverlayUiState.Failed)?.message ?: ""
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

            if (state.overlay is AccountOverlayUiState.Loading) Box(
                Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
        },
    ) {
        when (val profileState = state.profile) {
            is AccountDataUiState.Failed -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text(text = profileState.message) }

            AccountDataUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is AccountDataUiState.Success -> Column(Modifier.padding(20.dp)) {
                val profileData = profileState.data
                Row {
                    Column(Modifier.weight(1f)) {
                        Text(
                            profileData.name,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            profileData.division?.name ?: profileData.role.name,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { editExpanded = !editExpanded },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            InfoTooltipBox("Edit Profile") {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                        DropdownMenu(
                            expanded = editExpanded,
                            onDismissRequest = { editExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Profile", fontSize = 12.sp) },
                                onClick = {
                                    editExpanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Change Password", fontSize = 12.sp) },
                                onClick = {
                                    editExpanded = false
                                },
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        InfoTooltipBox("Logout") {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}