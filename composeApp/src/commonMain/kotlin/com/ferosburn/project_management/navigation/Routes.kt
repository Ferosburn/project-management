package com.ferosburn.project_management.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
sealed interface MainNavRoute : AppRoute

@Serializable
@SerialName("Login")
data object LoginScreen : AppRoute

@Serializable
@SerialName("Dashboard")
data object DashboardScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("Projects")
data object ProjectsScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("Reports")
data object ReportsScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("Contributors")
data object ContributorsScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("Account")
data object AccountScreen : AppRoute, MainNavRoute