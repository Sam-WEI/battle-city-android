package com.samwdev.battlecity.core

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.utils.Logger
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BattleViewModel(
    context: Application,
    val appState: AppState,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(context) {

    private val _gameState: MutableStateFlow<GameStatus> = MutableStateFlow(GameStatus.Initializing)
    val gameState: StateFlow<GameStatus> = _gameState

    private lateinit var battleState: BattleState

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
    }

    fun initStage(stageName: String) {
        val json = MapParser.readJsonFile(getApplication(), stageName)
        val stageConfig = MapParser.parse(json)
        battleState = BattleState(stageConfig)
        _gameState.value = GameStatus.Ready
    }

    suspend fun start() {
        coroutineScope {
            launch {
                battleState.startBattle()
            }
            launch(viewModelScope.coroutineContext) {
                battleState.mapState.gameEventFlow.collect { event ->
                    Logger.error("Event: $event")
                    when (event) {
                        GameOver -> {
                            appState.navController.navigateUp()
                            appState.navController.navigate(Route.Scoreboard)
                        }
                        MapCleared -> {

                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun resume() {

    }

    fun pause() {

    }
}

enum class GameStatus {
    Initializing,
    Ready,
}