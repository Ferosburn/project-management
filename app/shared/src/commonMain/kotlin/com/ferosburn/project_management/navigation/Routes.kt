package com.ferosburn.project_management.navigation

import com.ferosburn.project_management.models.user.UserRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
sealed interface MainNavRoute : AppRoute

@Serializable
sealed interface RootRoute : AppRoute

@Serializable
@SerialName("Login")
data object LoginScreen : AppRoute, RootRoute

@Serializable
@SerialName("Splash")
data object SplashScreen : AppRoute, RootRoute

@Serializable
@SerialName("Dashboard")
data object DashboardScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("Projects")
data object ProjectsScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("ProjectDetail")
data object ProjectDetailScreen : AppRoute

@Serializable
@SerialName("Reports")
data object ReportsScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("Contributors")
data object ContributorsScreen : AppRoute, MainNavRoute

@Serializable
@SerialName("ContributorDetail")
data class ContributorDetailScreen(val userId: String) : AppRoute

@Serializable
@SerialName("ContributorManage")
data class ContributorManageScreen(val user: UserRes? = null) : AppRoute

@Serializable
@SerialName("Account")
data object AccountScreen : AppRoute, MainNavRoute