package com.samwdev.battlecity.core

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class BattleViewModel(
    context: Application,
) : AndroidViewModel(context) {
    lateinit var battle: Battle
        private set

    private val _navFlow: MutableStateFlow<NavEvent?> = MutableStateFlow(null)
    val navFlow: StateFlow<NavEvent?> = _navFlow.asStateFlow()

    var tickerJob: Job? = null
    var paused: Boolean = false
    var lastBattleResult: BattleResult = BattleResult.Won

    val gameState: GameState = GameState(this)
    val mapState: MapState get() = battle.mapState
    val botState: BotState get() = battle.botState
    val tickState: TickState get() = battle.tickState
    val scoreState: ScoreState get() = battle.scoreState
    val bulletState: BulletState get() = battle.bulletState
    val tankState: TankState get() = battle.tankState
    val explosionState: ExplosionState get() = battle.explosionState
    val powerUpState: PowerUpState get() = battle.powerUpState
    val handheldControllerState: HandheldControllerState get() = battle.handheldControllerState

    val currentStageName: String? get() = currStageConfig?.name

    var currentGameStatus: GameStatus by mutableStateOf(StageCurtain)
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

    fun loadStage(stageName: String) {
        currentGameStatus = StageCurtain
        val json = try {
            MapParser.readJsonFile(getApplication(), stageName)
        } catch (e: IOException) {
            navigate(NavEvent.Landing)
            return
        }
        val stageConfig = MapParser.parse(json)
        prevStageConfig = currStageConfig
        currStageConfig = stageConfig
        battle = Battle(gameState, currStageConfig!!)

        navigate(NavEvent.BattleScreen)
    }

    private fun goToNextStage() {
        val nextStageName = (currentStageName!!.toInt() + 1).toString()
        loadStage(nextStageName)
    }

    fun navigate(navEvent: NavEvent) {
        _navFlow.value = navEvent
    }

    fun start() {
        currentGameStatus = InGame
        gameState.gameStarted = true

        tickerJob = viewModelScope.launch {
            battle.startBattle()
        }
    }

    fun setGameResult(result: BattleResult) {
        gameState.updateAfterBattle(battle)
        lastBattleResult = result
        when (result) {
            BattleResult.Won -> {
                showScoreboard()
            }
            BattleResult.Lost -> {
                // wait for the animation to finish and then the it will go to Scoreboard screen at the end of animation
                currentGameStatus = AnimatingGameOver
            }
        }
    }

    fun showScoreboard() {
        tickerJob?.cancel()
        currentGameStatus = ScoreboardDisplay
        navigate(NavEvent.Up)
        navigate(NavEvent.Scoreboard)
    }

    fun scoreboardCompleted() {
        when (lastBattleResult) {
            BattleResult.Won -> {
                currentGameStatus = StageCurtain
                goToNextStage()
            }
            BattleResult.Lost -> {
                navigate(NavEvent.Landing)
            }
        }
    }

    fun resume() {
        battle.resume()
    }

    fun pause() {
        battle.pause()
    }
}

sealed class GameStatus(val index: Int) {
    operator fun compareTo(other: GameStatus) = index - other.index
}
object StageCurtain : GameStatus(1)
object InGame : GameStatus(4)
object AnimatingGameOver : GameStatus(5)
object ScoreboardDisplay : GameStatus(10)

enum class BattleResult {
    Won, Lost
}