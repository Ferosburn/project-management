package com.ferosburn.project_management.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.github.terrakok.navigation3.browser.*
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@Composable
actual fun BrowserIntegration(navState: NavState) {
    val isMobile = remember { isMobileBrowser() }
    fun extracted() {
        val current = navState.currentBackstack
        val lastItem = current.lastOrNull()
        if (current.size > 1 && lastItem != null && lastItem is RootRoute) {
            navState.topLevelRoute = null
            navState.defaultBackstack.clear()
            navState.defaultBackstack.add(lastItem)
            navState.currentBackstack.clear()
            navState.currentBackstack.add(lastItem)
        }
    }

    print("currentBackstack: " + navState.currentBackstack.toList())
    print("topLevelRoute: " + navState.topLevelRoute)
    print("primaryMainNavRoute: " + navState.primaryMainNavRoute)
    print("defaultBackstack: " + navState.defaultBackstack.toList())
    print("mainNavBackStacks: \n" + navState.mainNavBackStacks.entries.joinToString("\n") { ">" + it.value.toList() })

    if (isMobile) {
        HierarchicalBrowserNavigation(
            currentDestination = remember { derivedStateOf { navState.currentBackstack.lastOrNull() } },
            currentDestinationName = { it?.toBrowserHistoryFragment() },
        )
    } else {
        extracted()
        ChronologicalBrowserNavigation(
            backStack = navState.currentBackstack,
            saveKey = { key -> key.toBrowserHistoryFragment() },
            restoreKey = { fragment ->
                val name = getBrowserHistoryFragmentName(fragment)
                val params = getBrowserHistoryFragmentParameters(fragment)


                when (name) {
                    AccountScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> AccountScreen
                    ContributorsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ContributorsScreen
                    DashboardScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> DashboardScreen
                    LoginScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> LoginScreen
                    ProjectsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ProjectsScreen
                    ProjectDetailScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ProjectDetailScreen
                    ReportsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ReportsScreen
                    ContributorDetailScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ContributorDetailScreen(
                        params["userId"] ?: ""
                    )

                    ContributorManageScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ContributorManageScreen()
                    SplashScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> SplashScreen
                    ProjectDetailScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ProjectDetailScreen(
                        params["projectId"] ?: ""
                    )

                    ProjectManageScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase() -> ProjectManageScreen()
                    else -> null
                } as AppRoute?
            },
        )
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun isMobileBrowser(): Boolean = js(
    """{
          if (navigator.userAgentData && navigator.userAgentData.mobile) {
            return navigator.userAgentData.mobile;
          }
          if (window.matchMedia && window.matchMedia('(pointer: coarse)').matches) {
            return window.innerWidth <= 820;
          }
          return /Mobi|Android|iPhone|iPad|iPod|IEMobile|Opera Mini|CriOS/i.test(navigator.userAgent);
      }"""
)

private fun AppRoute.toBrowserHistoryFragment() = when (val key = this) {
    AccountScreen -> buildBrowserHistoryFragment(AccountScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    ContributorsScreen -> buildBrowserHistoryFragment(ContributorsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    DashboardScreen -> buildBrowserHistoryFragment(DashboardScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    LoginScreen -> buildBrowserHistoryFragment(LoginScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    ProjectsScreen -> buildBrowserHistoryFragment(ProjectsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    ProjectDetailScreen -> buildBrowserHistoryFragment(ProjectsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    ReportsScreen -> buildBrowserHistoryFragment(ReportsScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    is ContributorDetailScreen -> buildBrowserHistoryFragment(ContributorDetailScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    is ContributorManageScreen -> buildBrowserHistoryFragment(ContributorManageScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    SplashScreen -> buildBrowserHistoryFragment(SplashScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    is ProjectDetailScreen -> buildBrowserHistoryFragment(ProjectDetailScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
    is ProjectManageScreen -> buildBrowserHistoryFragment(ProjectManageScreen.serializer().descriptor.serialName.fromPascalCaseToSnakeCase())
}

private fun String.fromPascalCaseToSnakeCase(): String {
    return replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2").replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}