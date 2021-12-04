package com.samwdev.battlecity.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
): AppState {
    return remember(Unit) {
        AppState(
            navController = navController
        )
    }
}

class AppState(
    val navController: NavHostController,
) {
    private val _gameEventFlow = MutableStateFlow<GameEvent>(Playing)
    val gameEventFlow: StateFlow<GameEvent> = _gameEventFlow

    fun sendGameEvent(event: GameEvent) {
        _gameEventFlow.value = event
    }
}