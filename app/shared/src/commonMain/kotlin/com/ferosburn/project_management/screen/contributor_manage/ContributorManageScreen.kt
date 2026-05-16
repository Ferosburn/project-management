package com.ferosburn.project_management.screen.contributor_manage

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.misc.SimpleCountDown
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.component.utils.clearFocusOnTap
import com.ferosburn.project_management.models.user.UserDivision
import com.ferosburn.project_management.models.user.UserRes
import com.ferosburn.project_management.models.user.UserRole
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributorManageScreen(
    user: UserRes?,
    onBack: () -> Unit,
) {
    val viewModel = remember { ContributorManageViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (user != null) viewModel.fillForm(user)
    }

    LaunchedEffect(state) {
        if (state is ContributorManageUiState.Success) {
            onBack()
        }
    }

    Box(
        Modifier.clearFocusOnTap(),
        contentAlignment = Alignment.TopCenter,
    ) {
        AppScaffold(
            title = if (user != null) "Edit" else "Add",
            onBack = onBack,
            sideContent = {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { if (user != null) viewModel.updateUser(user.id) else viewModel.createUser() },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Save", fontSize = 14.sp)
                    }
                    if (user != null) {
                        Button(
                            onClick = { viewModel.updateStatus(user.id, user.isActive) },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(if (user.isActive) "Deactivate User" else "Activate User", fontSize = 14.sp)
                        }
                        Button(
                            onClick = { viewModel.showDeleteDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red, contentColor = Color.White
                            )
                        ) {
                            Text("Delete User", fontSize = 14.sp)
                        }
                    }
                }
            },
        ) {
            Column(Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Name", fontSize = 12.sp) },
                    supportingText = {
                        if (!viewModel.nameError.isNullOrBlank()) Text(
                            text = viewModel.nameError!!, fontSize = 12.sp
                        )
                    },
                    isError = !viewModel.nameError.isNullOrBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text("Username", fontSize = 12.sp) },
                    supportingText = {
                        if (!viewModel.usernameError.isNullOrBlank()) Text(
                            text = viewModel.usernameError!!, fontSize = 12.sp
                        )
                    },
                    isError = !viewModel.usernameError.isNullOrBlank(),
                    singleLine = true,
                    enabled = user == null,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
                ExposedDropdownMenuBox(
                    expanded = viewModel.roleDropdownExpanded,
                    onExpandedChange = { viewModel.onRoleExpandChange() },
                ) {
                    OutlinedTextField(
                        value = viewModel.role?.name ?: "",
                        onValueChange = {},
                        label = { Text("Role", fontSize = 12.sp) },
                        supportingText = {
                            if (!viewModel.roleError.isNullOrBlank()) Text(
                                text = viewModel.roleError!!, fontSize = 12.sp
                            )
                        },
                        isError = !viewModel.roleError.isNullOrBlank(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.roleDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        textStyle = TextStyle(fontSize = 12.sp),
                    )
                    ExposedDropdownMenu(
                        expanded = viewModel.roleDropdownExpanded,
                        onDismissRequest = {
                            viewModel.roleDropdownExpanded = false
                            focusManager.clearFocus()
                        },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Choose one", fontSize = 12.sp) },
                            onClick = {
                                viewModel.onRoleChange(null)
                                focusManager.clearFocus()
                            },
                        )
                        UserRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name, fontSize = 12.sp) },
                                onClick = {
                                    viewModel.onRoleChange(role)
                                    focusManager.clearFocus()
                                },
                            )
                        }
                    }
                }
                if (viewModel.role == UserRole.DEVELOPER) ExposedDropdownMenuBox(
                    expanded = viewModel.divisionDropdownExpanded,
                    onExpandedChange = { viewModel.onDivisionExpandChange() },
                ) {
                    OutlinedTextField(
                        value = viewModel.division?.name ?: "",
                        onValueChange = {},
                        label = { Text("Division", fontSize = 12.sp) },
                        supportingText = {
                            if (!viewModel.divisionError.isNullOrBlank()) Text(
                                text = viewModel.divisionError!!, fontSize = 12.sp
                            )
                        },
                        isError = !viewModel.divisionError.isNullOrBlank(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.divisionDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        textStyle = TextStyle(fontSize = 12.sp),
                    )
                    ExposedDropdownMenu(
                        expanded = viewModel.divisionDropdownExpanded,
                        onDismissRequest = {
                            viewModel.divisionDropdownExpanded = false
                            focusManager.clearFocus()
                        },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Choose one", fontSize = 12.sp) },
                            onClick = {
                                viewModel.onDivisionChange(null)
                                focusManager.clearFocus()
                            },
                        )
                        UserDivision.entries.forEach { division ->
                            DropdownMenuItem(
                                text = { Text(division.name, fontSize = 12.sp) },
                                onClick = {
                                    viewModel.onDivisionChange(division)
                                    focusManager.clearFocus()
                                },
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state is ContributorManageUiState.Failed,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            Box(
                Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
            ) {
                val errorMessage = (state as? ContributorManageUiState.Failed)?.message ?: ""
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

        if (state is ContributorManageUiState.Loading) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        if (viewModel.showDeleteDialog && user != null) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable(onClick = {
                viewModel.showDeleteDialog = false
                viewModel.countDownFinished = false
            }),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.padding(20.dp).widthIn(max = 400.dp).fillMaxWidth()
                    .shadow(10.dp, shape = RoundedCornerShape(10.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Delete User Permanently?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("You can not reverse this action!")
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Button(
                        onClick = {
                            viewModel.showDeleteDialog = false
                            viewModel.countDownFinished = false
                            viewModel.deleteUser(user.id)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = viewModel.countDownFinished,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray,
                        )
                    ) {
                        if (viewModel.countDownFinished) {
                            Text("Delete", fontSize = 14.sp)
                        } else {
                            SimpleCountDown(10.seconds, onFinish = { viewModel.countDownFinished = true })
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            viewModel.showDeleteDialog = false
                            viewModel.countDownFinished = false
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}