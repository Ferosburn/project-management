package com.ferosburn.project_management.component.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> MainNavigationRail(
    currentDestination: MainNavigationItem<T>?,
    destinations: List<MainNavigationItem<T>>,
    onSelect: (MainNavigationItem<T>) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        if (expanded) 220.dp else 140.dp,
        animationSpec = spring(visibilityThreshold = Dp.VisibilityThreshold, stiffness = Spring.StiffnessLow),
    )

    Column(
        modifier.width(width).padding(12.dp).animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        destinations.forEach { destination ->
            NavigationRailMenuItem(
                iconRes = destination.icon,
                iconSelectedRes = destination.iconSelected,
                label = destination.label,
                isSelected = destination == currentDestination,
                isExpanded = expanded,
                onClick = { onSelect(destination) },
            )
        }
    }
}

@Composable
private fun NavigationRailMenuItem(
    iconRes: ImageVector,
    iconSelectedRes: ImageVector,
    label: String,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(
        modifier.selectable(
            selected = isSelected,
            enabled = !isSelected,
            role = Role.Tab,
            onClick = onClick,
        )
    ) {
        val itemContent = @Composable {
            Icon(
                imageVector = if (isSelected) iconSelectedRes else iconRes,
                contentDescription = null,
            )
            Text(label)
        }

        if (isExpanded) {
            Row {
                itemContent()
            }
        } else {
            Column {
                itemContent()
            }
        }
    }
}