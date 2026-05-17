package com.ferosburn.project_management.screen.project_manage

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.component.utils.clearFocusOnTap
import com.ferosburn.project_management.models.project.ProjectRes

@Composable
fun ProjectManageScreen(
    project: ProjectRes?,
    onBack: () -> Unit,
) {
    val viewModel = remember { ProjectManageViewModel() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (project != null) viewModel.fillForm(project)
    }

    LaunchedEffect(state) {
        if (state is ProjectManageUiState.Success) onBack()
    }

    Box(
        Modifier.clearFocusOnTap(),
        contentAlignment = Alignment.TopCenter,
    ) {
        AppScaffold(
            title = if (project != null) "Edit" else "Add",
            onBack = onBack,
            sideContent = {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { if (project != null) viewModel.updateProject(project.id) else viewModel.createProject() },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Save", fontSize = 14.sp)
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
                    value = viewModel.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Description", fontSize = 12.sp) },
                    supportingText = {
                        if (!viewModel.descriptionError.isNullOrBlank()) Text(
                            text = viewModel.descriptionError!!, fontSize = 12.sp
                        )
                    },
                    isError = !viewModel.descriptionError.isNullOrBlank(),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 12.sp),
                )
            }
        }

        AnimatedVisibility(
            visible = state is ProjectManageUiState.Failed,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            Box(
                Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                    .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
            ) {
                val errorMessage = (state as? ProjectManageUiState.Failed)?.message ?: ""
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

        if (state is ProjectManageUiState.Loading) Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}