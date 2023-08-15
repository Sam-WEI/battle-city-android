package com.samwdev.battlecity.core

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.utils.Logger
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BattleViewModel(
    context: Application,
) : AndroidViewModel(context) {
    lateinit var battleState: BattleState
        private set

    private val _navFlow: MutableStateFlow<NavEvent?> = MutableStateFlow(null)
    val navFlow: StateFlow<NavEvent?> = _navFlow.asStateFlow()

    val gameState: GameState = GameState(this)

    val mapState: MapState get() = battleState.mapState
    val botState: BotState get() = battleState.botState
    val tickState: TickState get() = battleState.tickState
    val scoreState: ScoreState get() = battleState.scoreState
    val bulletState: BulletState get() = battleState.bulletState
    val tankState: TankState get() = battleState.tankState
    val explosionState: ExplosionState get() = battleState.explosionState
    val powerUpState: PowerUpState get() = battleState.powerUpState
    val handheldControllerState: HandheldControllerState get() = battleState.handheldControllerState

    val currentStageName: String? get() = currStageConfig?.name

    var currentGameStatus: GameStatus by mutableStateOf(Initial)
        private set
    var prevStageConfig: StageConfig? = null
        private set
    var currStageConfig: StageConfig? = null
        private set

    var debugConfig: DebugConfig by mutableStateOf(DebugConfig(
        showFps = true,
        showPivotBox = false,
        maxBot = 4,
        showAccessPoints = false,
        showWaypoints = true,
    ))

    fun loadStageData(stageName: String) {
        if (currentGameStatus != Initial && currentGameStatus != MapCleared) return
        val json = MapParser.readJsonFile(getApplication(), stageName)
        val stageConfig = MapParser.parse(json)
        prevStageConfig = currStageConfig
        currStageConfig = stageConfig
        battleState = BattleState(gameState, currStageConfig!!)

        currentGameStatus = StageDataLoaded
    }

    fun goToNextStage() {
        currentGameStatus = Initial
        val nextStageName = (currentStageName!!.toInt() + 1).toString()
        navigate(NavEvent.BattleScreen(nextStageName))
    }

    fun navigate(navEvent: NavEvent) {
        _navFlow.value = navEvent
    }

    fun loadStageData() {
//        val json = MapParser.readJsonFile(getApplication(), currentStageName!!)
//        val stageConfig = MapParser.parse(json)
//        currentGameStatus = StageDataLoaded
    }

    suspend fun start() {
        coroutineScope {
            launch {
                battleState.startBattle()
                currentGameStatus = Playing
            }

            launch(viewModelScope.coroutineContext) {
                gameState.inGameEventFlow.collect { event ->
                    Logger.error("Event: $event")
                    when (event) {
                        GameOver -> {
//                            battleState.pause()
                            currentGameStatus = GameOver

                        }
                        MapCleared -> {
                            currentGameStatus = MapCleared
                            navigate(NavEvent.Up)
                            navigate(NavEvent.Scoreboard)
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
        currentGameStatus = Paused
    }
}

sealed class GameStatus(val index: Int) {
    operator fun compareTo(other: GameStatus) = index - other.index
}
object Initial : GameStatus(0)
//data class StageSelected(val stageName: String) : GameStatus()
object StageDataLoaded : GameStatus(1)
//object SwitchingStage : GameStatus(2)
object Paused : GameStatus(3)
object Playing : GameStatus(4)

object MapCleared : GameStatus(5)
object GameOver : GameStatus(5)
