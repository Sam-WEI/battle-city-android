package com.samwdev.battlecity.ui.components

import android.app.Application
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.SavedStateRegistryOwner
import com.samwdev.battlecity.core.AppState
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.Route
import com.samwdev.battlecity.core.rememberAppState
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import com.samwdev.battlecity.utils.MapParser

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BattleCityApp() {
    val battleViewModel: BattleViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
        factory = provideBattleViewModel(appState = rememberAppState())
    )
    BattleCityTheme {
        NavHost(
            navController = battleViewModel.appState.navController,
            startDestination = Route.Landing,
        ) {
            composable(Route.Landing) {
                FullScreenWrapper {
                    LandingScreen { menuItem ->
                        // todo
                        battleViewModel.appState.navController.navigate("${Route.BattleScreen}/1") {
                            this.launchSingleTop = true
                        }
                    }
                }
            }
            composable(
                route = "${Route.BattleScreen}/{${Route.Key.StageName}}",
                arguments = listOf(navArgument(Route.Key.StageName) { type = NavType.StringType })
            ) { backStackEntry ->
                val stageName = backStackEntry.arguments?.getString(Route.Key.StageName)!!
                // the framework runs this block several times. Use LaunchedEffect to make it run only once.
                LaunchedEffect(stageName) {
                    battleViewModel.selectStage(stageName)
                }
                BattleScreen()
            }
            composable(
                route = Route.Scoreboard,
            ) { backStackEntry ->
                FullScreenWrapper {
                    ScoreboardScreen()
                }
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
fun provideBattleViewModel(appState: AppState): BattleViewModelFactory {
    return BattleViewModelFactory(LocalContext.current.applicationContext, appState, LocalSavedStateRegistryOwner.current)
}

class BattleViewModelFactory(
    private val context: Context,
    private val appState: AppState,
    owner: SavedStateRegistryOwner,
) : AbstractSavedStateViewModelFactory(owner, null) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return BattleViewModel(context as Application, appState, handle) as T
    }
}
