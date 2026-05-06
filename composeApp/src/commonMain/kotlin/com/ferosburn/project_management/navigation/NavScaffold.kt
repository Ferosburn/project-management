package com.ferosburn.project_management.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LogoDev
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.LogoDev
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ferosburn.project_management.component.navigation.MainNavigationBar
import com.ferosburn.project_management.component.navigation.MainNavigationItem
import com.ferosburn.project_management.component.navigation.MainNavigationRail
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize

private val destinations: List<MainNavigationItem<MainNavRoute>> = listOf(
    MainNavigationItem(
        label = "Dashboard",
        icon = Icons.Outlined.Dashboard,
        iconSelected = Icons.Filled.Dashboard,
        route = DashboardScreen,
    ),
    MainNavigationItem(
        label = "Projects",
        icon = Icons.Outlined.DeveloperBoard,
        iconSelected = Icons.Default.DeveloperBoard,
        route = ProjectsScreen,
    ),
    MainNavigationItem(
        label = "Reports",
        icon = Icons.AutoMirrored.Outlined.Assignment,
        iconSelected = Icons.AutoMirrored.Filled.Assignment,
        route = ReportsScreen,
    ),
    MainNavigationItem(
        label = "Contributors",
        icon = Icons.Outlined.LogoDev,
        iconSelected = Icons.Default.LogoDev,
        route = ContributorsScreen,
    ),
    MainNavigationItem(
        label = "Account",
        icon = Icons.Outlined.Engineering,
        iconSelected = Icons.Filled.Engineering,
        route = AccountScreen,
    ),
)

@Composable
fun NavScaffold(
    navState: NavState, navigator: Navigator, content: @Composable (() -> Unit)
) {
    val onSelectRoute: (MainNavRoute) -> Unit = { route ->
        navigator.activate(route)
    }
    val windowSize = LocalWindowSize.current
    val showLargeNavigation = windowSize != WindowSize.COMPACT && navState.topLevelRoute != null
    val showCompactNavigation =
        windowSize == WindowSize.COMPACT && navState.topLevelRoute != null && navState.currentBackstack.size == 1/* && !isKeyboardOpen()*/

    Row(
        Modifier.fillMaxSize()
    ) {
        val enterAnimSpec = tween<IntSize>(delayMillis = AnimationConstants.DefaultDurationMillis)

        AnimatedVisibility(
            visible = showLargeNavigation,
            enter = expandHorizontally(enterAnimSpec),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            SideNavigation(
                currentRoute = navState.topLevelRoute,
                /*destinations = destinations,*/
                onSelectRoute = onSelectRoute,
                expanded = windowSize == WindowSize.EXPANDED,
            )
        }
        Column(Modifier.weight(1f)) {
            Box(Modifier.weight(1f).clipToBounds()) {
                content()
            }
            AnimatedVisibility(
                visible = showCompactNavigation,
                enter = expandVertically(enterAnimSpec),
                exit = shrinkVertically() + fadeOut()
            ) {
                BottomNavigation(
                    currentRoute = navState.topLevelRoute,
                    /*destinations = destinations,*/
                    onSelectRoute = onSelectRoute,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigation(
    currentRoute: MainNavRoute?,
    /*destinations: List<MainNavigationItem<MainNavRoute>>,*/
    onSelectRoute: (route: MainNavRoute) -> Unit,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Column {
        HorizontalDivider(thickness = 1.dp)
        MainNavigationBar(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
        )
    }
}

@Composable
private fun SideNavigation(
    currentRoute: MainNavRoute?,
    /*destinations: List<MainNavigationItem<MainNavRoute>>,*/
    onSelectRoute: (route: MainNavRoute) -> Unit,
    expanded: Boolean = false,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Row {
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
            expanded = expanded,
        )
        VerticalDivider(thickness = 1.dp)
    }
}