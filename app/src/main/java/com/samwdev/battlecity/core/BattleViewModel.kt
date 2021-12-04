package com.samwdev.battlecity.core

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.utils.Logger
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BattleViewModel(
    context: Application,
    private val stageConfig: StageConfig,
    private val appState: AppState,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(context) {

    private var battleState: BattleState

    val mapState: MapState get() = battleState.mapState
    val botState: BotState get() = battleState.botState
    val tickState: TickState get() = battleState.tickState
    val scoreState: ScoreState get() = battleState.scoreState
    val bulletState: BulletState get() = battleState.bulletState
    val tankState: TankState get() = battleState.tankState
    val explosionState: ExplosionState get() = battleState.explosionState
    val powerUpState: PowerUpState get() = battleState.powerUpState
    val handheldControllerState: HandheldControllerState get() = battleState.handheldControllerState

    init {
        battleState = BattleState(stageConfig)
    }

    private fun init(stageName: String) {

    }

    suspend fun start() {
        battleState.startBattle()
        viewModelScope.launch {
            battleState.mapState.gameEventFlow.collect { event ->
                Logger.error("Event: $event")
                if (event == GameOver) {
                    appState.navController.navigateUp()
                    appState.navController.navigate(Route.Scoreboard)
//                    appState.navController.navigate("${Route.BattleScreen}/6") {
//                        this.launchSingleTop = true
//                    }
                }
            }
        }
    }

    fun resume() {

    }

    fun pause() {

    }
}