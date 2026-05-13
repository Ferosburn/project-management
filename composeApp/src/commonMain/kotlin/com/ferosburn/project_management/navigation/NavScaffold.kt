package com.ferosburn.project_management.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.LogoDev
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
    var isMobileNavOpen by remember { mutableStateOf(false) }
    LaunchedEffect(navState.topLevelRoute) {
        isMobileNavOpen = false
    }
    val showLargeNavigation = windowSize != WindowSize.COMPACT && navState.topLevelRoute != null
    val showMobileNav =
        windowSize == WindowSize.COMPACT && navState.topLevelRoute != null && navState.currentBackstack.size == 1/* && !isKeyboardOpen()*/

//    NavigationBackHandler(
//        isBackEnabled = isMobileNavOpen
//    ) {
//        isMobileNavOpen = false
//    }

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
        Box(
            Modifier.weight(1f).background(Color.LightGray.copy(alpha = 0.4f)).clipToBounds()
                .pointerInput(showMobileNav) {
                    if (!showMobileNav) return@pointerInput
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            isMobileNavOpen = if (dragAmount > 50f) true else false
                        },
                    )
                },
        ) {
            content()
            if (showMobileNav) {
                IconButton(
                    onClick = { isMobileNavOpen = true }, modifier = Modifier.align(Alignment.TopStart),
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Open navigation")
                }
                MobileNavOverlay(
                    isOpen = isMobileNavOpen, currentRoute = navState.topLevelRoute,
                    onSelectRoute = {
                        onSelectRoute(it)
                        isMobileNavOpen = false
                    },
                    onDismiss = { isMobileNavOpen = false },
                )
            }
        }
    }
}

@Composable
fun MobileNavOverlay(
    isOpen: Boolean,
    currentRoute: MainNavRoute?,
    onSelectRoute: (MainNavRoute) -> Unit,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable(
                indication = null, interactionSource = remember { MutableInteractionSource() }) { onDismiss() },
        )
    }
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { -it }, animationSpec = tween(300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it }, animationSpec = tween(300)
        ),
    ) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(0.75f).background(Color.White)) {
            SideNavigation(
                currentRoute = currentRoute,
                onSelectRoute = onSelectRoute,
                expanded = true,
            )
        }
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