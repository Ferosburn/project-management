package com.ferosburn.project_management.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer

class NavState(
    val mainNavBackStacks: Map<MainNavRoute, SnapshotStateList<AppRoute>>,
    val defaultBackstack: SnapshotStateList<AppRoute>,
    val primaryMainNavRoute: MainNavRoute,
    val currentBackstack: SnapshotStateList<AppRoute>,
) {
    var topLevelRoute: MainNavRoute?
        get() = currentBackstack.firstOrNull() as? MainNavRoute
        set(value) {
            val oldRoute = topLevelRoute

            val oldStorage = if (oldRoute != null) mainNavBackStacks[oldRoute]!! else defaultBackstack
            oldStorage.clear()
            oldStorage.addAll(currentBackstack)

            val newStorage = if (value != null) mainNavBackStacks[value]!! else defaultBackstack
            currentBackstack.clear()
            currentBackstack.addAll(newStorage)
        }

    @Composable
    fun toDecoratedEntries(
        entryProvider: (AppRoute) -> NavEntry<AppRoute>
    ): SnapshotStateList<NavEntry<AppRoute>> {
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<AppRoute>(),
            rememberViewModelStoreNavEntryDecorator(),
        )

        val topLevelEntries = mainNavBackStacks.mapValues { (route, stack) ->
            rememberDecoratedNavEntries(
                backStack = if (route == topLevelRoute) currentBackstack else stack,
                entryDecorators = decorators,
                entryProvider = entryProvider
            )
        }.withDefault { emptyList() }

        val defaultEntries = rememberDecoratedNavEntries(
            backStack = if (topLevelRoute == null) currentBackstack else defaultBackstack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )

        return when (val topRoute = topLevelRoute) {
            null -> defaultEntries
            primaryMainNavRoute -> topLevelEntries.getValue(primaryMainNavRoute)
            else -> topLevelEntries.getValue(primaryMainNavRoute) + topLevelEntries.getValue(topRoute)
        }.toMutableStateList()
    }
}

@Composable
fun rememberNavState(
    startRoute: AppRoute,
    primaryMainNavRoute: MainNavRoute,
    topLevelRoutes: Set<MainNavRoute>,
): NavState {

    val topLevelBackStacks: Map<MainNavRoute, SnapshotStateList<AppRoute>> = buildMap {
        topLevelRoutes.forEach { route ->
            put(route, rememberSerializable(serializer = SnapshotStateListSerializer()) {
                mutableStateListOf(route)
            })
        }
    }

    val defaultBackstack = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        if (startRoute !is MainNavRoute) {
            mutableStateListOf(startRoute)
        } else {
            mutableStateListOf()
        }
    }

    val currentBackstack = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        val restoredBackstack = if (startRoute is MainNavRoute) {
            topLevelBackStacks[startRoute]!!
        } else {
            defaultBackstack
        }
        restoredBackstack.toMutableStateList()
    }

    return remember(startRoute, topLevelRoutes) {
        NavState(
            primaryMainNavRoute = primaryMainNavRoute,
            mainNavBackStacks = topLevelBackStacks,
            defaultBackstack = defaultBackstack,
            currentBackstack = currentBackstack,
        )
    }
}