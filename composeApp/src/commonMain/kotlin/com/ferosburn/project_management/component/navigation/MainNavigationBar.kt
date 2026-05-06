package com.ferosburn.project_management.component.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> MainNavigationBar(
    currentDestination: MainNavigationItem<T>?,
    destinations: List<MainNavigationItem<T>>,
    onSelect: (MainNavigationItem<T>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        destinations.forEach { destination ->
            NavigationBarMenuItem(
                iconRes = destination.icon,
                iconSelectedRes = destination.iconSelected,
                label = destination.label,
                isSelected = destination == currentDestination,
                onClick = { onSelect(destination) },
            )
        }
    }
}

@Composable
private fun NavigationBarMenuItem(
    iconRes: ImageVector,
    iconSelectedRes: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.selectable(
            selected = isSelected,
            enabled = !isSelected,
            role = Role.Tab,
            onClick = onClick,
        )
    ) {
        Icon(
            imageVector = if (isSelected) iconSelectedRes else iconRes,
            contentDescription = null,
        )
        Text(label)
    }
}