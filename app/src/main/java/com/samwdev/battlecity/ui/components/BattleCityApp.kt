package com.samwdev.battlecity.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.samwdev.battlecity.core.Route
import com.samwdev.battlecity.core.rememberAppState
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import com.samwdev.battlecity.utils.MapParser
import kotlin.random.Random


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BattleCityApp() {
    val appState = rememberAppState()
    BattleCityTheme {
        var stageName = Random.nextInt(1, 36).toString()
        stageName = 2.toString()

        val json = MapParser.readJsonFile(LocalContext.current, stageName)
        val stageConfig = MapParser.parse(json)
        NavHost(
            navController = appState.navController,
            startDestination = Route.BattleScreen,
        ) {
            composable(Route.Home) {
                LandingScreen()
            }
            composable(Route.BattleScreen) {
                BattleScreen(stageConfig)
            }
            composable(Route.Scoreboard) {
                ScoreboardScreen()
            }
            composable(Route.GameOver) {
                GameOverScreen()
            }
        }
    }
}
