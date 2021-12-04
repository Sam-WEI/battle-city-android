package com.samwdev.battlecity.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.SavedStateRegistryOwner
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.Route
import com.samwdev.battlecity.core.rememberAppState
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import com.samwdev.battlecity.utils.MapParser
import kotlin.random.Random

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BattleCityApp() {
    val appState = rememberAppState()
    BattleCityTheme {
        NavHost(
            navController = appState.navController,
            startDestination = Route.Landing,
        ) {
            composable(Route.Landing) {
                FullScreenWrapper {
                    LandingScreen { menuItem ->
                        // todo
                        appState.navController.navigate("${Route.BattleScreen}/1") {
                            this.launchSingleTop = true
                        }
                    }
                }
            }
            composable(
                route = "${Route.BattleScreen}/{${Route.Key.StageId}}",
                arguments = listOf(navArgument(Route.Key.StageId) { type = NavType.StringType} )
            ) { backStackEntry ->
                val stageId = backStackEntry.arguments?.getString(Route.Key.StageId)!!
                val json = MapParser.readJsonFile(LocalContext.current, stageId)
                val stageConfig = MapParser.parse(json)
                BattleScreen(stageConfig)
            }
            composable(Route.Scoreboard) { backStackEntry ->
                ScoreboardScreen()
            }
            composable(Route.GameOver) {
                GameOverScreen()
            }
        }
    }
}

@Composable
fun FullScreenWrapper(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun provideBattleViewModel(stageConfig: StageConfig): BattleViewModelFactory {
    return BattleViewModelFactory(stageConfig, LocalSavedStateRegistryOwner.current)
}

class BattleViewModelFactory(
    private val stageConfig: StageConfig,
    owner: SavedStateRegistryOwner,
) : AbstractSavedStateViewModelFactory(owner, null) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return BattleViewModel(stageConfig, handle) as T
    }
}
