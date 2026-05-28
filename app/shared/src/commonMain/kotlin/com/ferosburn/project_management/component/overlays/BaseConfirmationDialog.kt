package com.ferosburn.project_management.component.overlays

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BaseConfirmationDialog(
    title: String,
    description: String? = null,
    onClose: () -> Unit,
    buttons: @Composable () -> Unit = {},
) {
    BaseDialogOverlay(
        onClose = onClose,
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        if (!description.isNullOrBlank()) {
            Text(description)
            Spacer(modifier = Modifier.height(20.dp))
        }
        buttons()
    }
}