package com.samwdev.battlecity.ui.components

import android.app.Application
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.samwdev.battlecity.core.AppState
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.NavEvent
import com.samwdev.battlecity.core.Route
import com.samwdev.battlecity.core.rememberAppState
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
        CompositionLocalProvider(LocalBattleViewModel provides viewModel()) {
            NavHost(
                navController = navController,
                startDestination = Route.Landing,
            ) {
                composable(Route.Landing) {
                    FullScreenWrapper {
                        LandingScreen { menuItem ->
                            when (menuItem) {
                                LandingScreenMenuItem.Player1, LandingScreenMenuItem.Player2 -> {
                                    battleViewModel.navigate(NavEvent.BattleScreen("24"))
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
                    route = "${Route.BattleScreen}/{${Route.Key.StageName}}",
                    arguments = listOf(navArgument(Route.Key.StageName) { type = NavType.StringType })
                ) { backStackEntry ->
                    val stageName = backStackEntry.arguments?.getString(Route.Key.StageName)!!
                    // the framework runs this block several times. Use LaunchedEffect to make it run only once.
                    LaunchedEffect(stageName) {
                        battleViewModel.loadStageData(stageName)
                    }
                    BattleScreen()
                }
                composable(
                    route = Route.MapSelection,
                ) { backStackEntry ->
                    FullScreenWrapper {
                        MapSelectionScreen {
                            battleViewModel.navigate(NavEvent.BattleScreen(it.name))
                        }
                    }
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

// todo
//@Composable
//fun provideBattleViewModel(appState: AppState): BattleViewModelFactory {
//    return BattleViewModelFactory(LocalContext.current.applicationContext, appState, LocalSavedStateRegistryOwner.current)
//}
//
//class BattleViewModelFactory(
//    private val context: Context,
//    private val appState: AppState,
//    owner: SavedStateRegistryOwner,
//) : AbstractSavedStateViewModelFactory(owner, null) {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
//        return BattleViewModel(context as Application) as T
//    }
//}
