package com.ferosburn.project_management.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferosburn.project_management.component.scaffold.AppScaffold
import com.ferosburn.project_management.models.user.UserRes

@Composable
fun ContributorDetailScreen(
    user: UserRes,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = user.name,
        onBack = onBack,
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("id: ${user.id}")
                Text("name: ${user.name}")
                Text("username: ${user.username}")
                Text("role: ${user.role.name}")
                Text("division: ${user.division?.name ?: "-"}")
                Text("isActive: ${user.isActive}")
            }
        }
    }
}