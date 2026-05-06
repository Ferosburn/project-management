package com.ferosburn.project_management.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ferosburn.project_management.screen.AccountScreen
import com.ferosburn.project_management.screen.ContributorsScreen
import com.ferosburn.project_management.screen.DashboardScreen
import com.ferosburn.project_management.screen.LoginScreen
import com.ferosburn.project_management.screen.ProjectsScreen
import com.ferosburn.project_management.screen.ReportsScreen

@Composable
internal fun NavHost(
    isLoggedIn: Boolean,
) {
    val startRoute = remember { if (isLoggedIn) DashboardScreen else LoginScreen }
    val navState = rememberNavState(
        startRoute = startRoute,
        topLevelRoutes = setOf(
            DashboardScreen,
            ProjectsScreen,
            ReportsScreen,
            ContributorsScreen,
            AccountScreen,
        ),
        primaryMainNavRoute = DashboardScreen,
    )
    val navigator = remember(navState) {
        Navigator(navState)
    }
    val entryProvider = entryProvider {
        screens(
            navigator = navigator,
            onBack = { navigator.goBack() },
        )
    }
    MaterialTheme {
        NavScaffold(
            navState = navState,
            navigator = navigator,
        ) {
            NavDisplay(
                entries = navState.toDecoratedEntries(entryProvider),
                onBack = navigator::goBack,
            )
        }
    }
}

private fun EntryProviderScope<AppRoute>.screens(
    navigator: Navigator,
    onBack: () -> Unit
) {
    entry<LoginScreen> {
        LoginScreen()
    }
    entry<DashboardScreen> {
        DashboardScreen()
    }
    entry<ProjectsScreen> {
        ProjectsScreen()
    }
    entry<ReportsScreen> {
        ReportsScreen()
    }
    entry<ContributorsScreen> {
        ContributorsScreen()
    }
    entry<AccountScreen> {
        AccountScreen()
    }
}
