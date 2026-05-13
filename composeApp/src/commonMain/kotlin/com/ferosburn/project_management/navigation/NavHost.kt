package com.ferosburn.project_management.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ferosburn.project_management.screen.*
import com.ferosburn.project_management.screen.account.AccountScreen
import com.ferosburn.project_management.screen.login.LoginScreen
import com.ferosburn.project_management.screen.splash.SplashScreen
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
internal fun NavHost(
    /*isLoggedIn: Boolean,*/
) {
    /*val startRoute = remember { if (isLoggedIn) DashboardScreen else LoginScreen }*/
    val startRoute = remember { SplashScreen }
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
    BrowserIntegration(navState)
    val entryProvider = entryProvider {
        screens(
            navigator = navigator,
            onBack = { navigator.goBack() },
        )
    }
    LaunchedEffect(Unit) {
        NavChannel.channel.receiveAsFlow().collect { event ->
            when (event) {
                NavEvent.ToLogin -> navigator.reset(LoginScreen)
                NavEvent.ToDashboard -> navigator.reset(DashboardScreen)
            }
        }
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
    onBack: () -> Unit,
) {
    entry<SplashScreen> {
        SplashScreen()
    }
    entry<LoginScreen> {
        LoginScreen(
            onLoginSuccess = { navigator.set(DashboardScreen) },
        )
    }
    entry<DashboardScreen> {
        DashboardScreen(
            logout = { navigator.set(LoginScreen) },
        )
    }
    entry<ProjectsScreen> {
        ProjectsScreen(
            onProjectClick = { navigator.add(ProjectDetailScreen) },
        )
    }
    entry<ProjectDetailScreen> {
        ProjectDetailScreen()
    }
    entry<ReportsScreen> {
        ReportsScreen()
    }
    entry<ContributorsScreen> {
        ContributorsScreen()
    }
    entry<AccountScreen> {
        AccountScreen(
            onLogoutSuccess = { navigator.reset(LoginScreen) },
        )
    }
}
