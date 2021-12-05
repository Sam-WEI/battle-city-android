package com.samwdev.battlecity.core

import androidx.compose.runtime.*
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

// todo remove this class all together
class AppState(
    val navController: NavHostController,
) {
}
