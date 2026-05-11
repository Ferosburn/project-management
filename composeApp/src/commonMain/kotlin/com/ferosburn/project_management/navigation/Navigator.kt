package com.ferosburn.project_management.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class Navigator(
    val state: NavState
) {
    private val _tabReselections = MutableSharedFlow<MainNavRoute>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    fun goBack() {
        val currentBackstack = state.currentBackstack

        if (state.topLevelRoute == null) {
            // We're using the default stack, remove an entry if possible
            if (currentBackstack.size > 1) {
                currentBackstack.removeLastOrNull()
            }
            return
        }
        if (currentBackstack.size == 1 && state.topLevelRoute != state.primaryMainNavRoute) {
            // Can't go further up on current backstack, but we're not on the primary route
            /*if (mainNavBackStacks) {
                // Go back to the primary top-level route if enabled
                state.topLevelRoute = state.primaryMainNavRoute
            }*/
        } else if (currentBackstack.size > 1) {
            currentBackstack.removeLastOrNull()
        }
    }

    fun add(route: AppRoute) {
        if (route is MainNavRoute) {
            activate(route)
        } else {
            state.currentBackstack.add(route)
        }
    }

    fun set(route: AppRoute) {
        state.currentBackstack.clear()
        add(route)
    }

    fun activate(route: MainNavRoute, withReselection: Boolean = true) {
        if (withReselection && route == state.topLevelRoute) {
            val currentBackstack = state.currentBackstack

            // Reselected the current top-level route, clear to root
            if (currentBackstack.size > 1) {
                currentBackstack.removeRange(1, currentBackstack.size)
            } else {
                // Already at root, signal reselection for scroll-to-top
                _tabReselections.tryEmit(route)
            }
            return
        }
        state.topLevelRoute = route
    }
}