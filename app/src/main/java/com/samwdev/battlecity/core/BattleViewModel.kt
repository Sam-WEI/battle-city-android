package com.samwdev.battlecity.core

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.utils.Logger
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BattleViewModel(
    context: Application,
    val appState: AppState,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(context) {

    lateinit var battleState: BattleState
        private set

    val mapState: MapState get() = battleState.mapState
    val botState: BotState get() = battleState.botState
    val tickState: TickState get() = battleState.tickState
    val scoreState: ScoreState get() = battleState.scoreState
    val bulletState: BulletState get() = battleState.bulletState
    val tankState: TankState get() = battleState.tankState
    val explosionState: ExplosionState get() = battleState.explosionState
    val powerUpState: PowerUpState get() = battleState.powerUpState
    val handheldControllerState: HandheldControllerState get() = battleState.handheldControllerState

    var currentStageName: String? = null
        private set
    var currentGameStatus: GameStatus by mutableStateOf(Initial)
        private set

    fun selectStage(stageName: String) {
        if (currentGameStatus != Initial && currentGameStatus != MapCleared) return
        currentStageName = stageName
        currentGameStatus = StageSelected(stageName)
    }

    fun nextStage() {
        val nextStageName = (currentStageName!!.toInt() + 1).toString()
        appState.navController.navigate("${Route.BattleScreen}/$nextStageName")
    }

    fun initStage() {
        requireNotNull(currentStageName) { "Stage not selected" }

        val json = MapParser.readJsonFile(getApplication(), currentStageName!!)
        val stageConfig = MapParser.parse(json)
        battleState = BattleState(stageConfig)

        currentGameStatus = ReadyToPlay
    }

    suspend fun start() {
//        require(currentGameStatus == ReadyToPlay)
        currentGameStatus = Playing
        coroutineScope {
            launch {
                battleState.startBattle()
            }
            launch(viewModelScope.coroutineContext) {
                mapState.inGameEventFlow.collect { event ->
                    Logger.error("Event: $event")
                    when (event) {
                        GameOver -> {
                            battleState.pause()
                            currentGameStatus = MapCleared

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
        battleState.resume()
        currentGameStatus = Playing
    }

    fun pause() {
        battleState.pause()
        currentGameStatus = ReadyToPlay
    }
}

sealed class GameStatus
object Initial : GameStatus()
data class StageSelected(val stageName: String) : GameStatus()
object ReadyToPlay : GameStatus()
object Playing : GameStatus()
object MapCleared : GameStatus()
object GameOver : GameStatus()