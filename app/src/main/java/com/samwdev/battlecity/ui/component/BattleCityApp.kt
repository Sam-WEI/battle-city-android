package com.samwdev.battlecity.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.NavEvent
import com.samwdev.battlecity.core.Route
import com.samwdev.battlecity.ui.screen.BattleScreen
import com.samwdev.battlecity.ui.screen.GameOverScreen
import com.samwdev.battlecity.ui.screen.LandingScreen
import com.samwdev.battlecity.ui.screen.LandingScreenMenuItem
import com.samwdev.battlecity.ui.screen.MapSelectionScreen
import com.samwdev.battlecity.ui.screen.ScoreboardScreen
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun BattleCityApp() {
    val navController = rememberNavController()
    val battleViewModel: BattleViewModel = viewModel()

    val nextNav = battleViewModel.navFlow.collectAsState().value
    LaunchedEffect(nextNav) {
        when (nextNav) {
            NavEvent.Up -> navController.navigateUp()
            is NavEvent.Routed -> navController.navigate(nextNav.route) {
                launchSingleTop = true
            }
            null -> {}
        }
    }

    BattleCityTheme {
        CompositionLocalProvider(LocalBattleViewModel provides battleViewModel) {
            NavHost(
                navController = navController,
                startDestination = Route.Landing,
            ) {
                composable(Route.Landing) {
                    FullScreenWrapper {
                        LandingScreen { menuItem ->
                            when (menuItem) {
                                LandingScreenMenuItem.Player1, LandingScreenMenuItem.Player2 -> {
                                    battleViewModel.loadStage("1", true)
                                }
                                LandingScreenMenuItem.Stages -> {
                                    battleViewModel.navigate(NavEvent.MapSelection)
                                }
                                LandingScreenMenuItem.Editor -> {

                                }
                            }
                        }
                    }
                }
                composable(
                    route = Route.BattleScreen,
                ) {
                    FullScreenWrapper {
                        BattleScreen()
                    }
                }
                composable(
                    route = Route.MapSelection,
                ) {
                    FullScreenWrapper {
                        MapSelectionScreen {
                            battleViewModel.loadStage(it.name, true)
                        }
                    }
                }
                composable(
                    route = Route.Scoreboard,
                ) {
                    FullScreenWrapper {
                        ScoreboardScreen()
                    }
                }
                composable(Route.GameOver) {
                    FullScreenWrapper {
                        GameOverScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenWrapper(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(27, 27, 27, 255)),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

val LocalBattleViewModel = staticCompositionLocalOf<BattleViewModel> { error("Not provided") }
