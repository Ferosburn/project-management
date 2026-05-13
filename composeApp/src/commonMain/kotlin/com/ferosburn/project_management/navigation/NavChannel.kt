package com.ferosburn.project_management.navigation

import kotlinx.coroutines.channels.Channel

sealed interface NavEvent {
    data object ToLogin : NavEvent
    data object ToDashboard : NavEvent
}

object NavChannel {
    val channel = Channel<NavEvent>(1)

    fun navigateToLogin() {
        channel.trySend(NavEvent.ToLogin)
    }

    fun navigateToDashboard() {
        channel.trySend(NavEvent.ToDashboard)
    }
}