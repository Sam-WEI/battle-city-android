package com.samwdev.battlecity.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
): AppState {
    return remember {
        AppState(
            navController = navController
        )
    }
}

class AppState(
    val navController: NavHostController,
) {

}