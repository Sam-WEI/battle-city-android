package com.samwdev.battlecity.core

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.core.state.BotState
import com.samwdev.battlecity.core.state.BulletState
import com.samwdev.battlecity.core.state.ExplosionState
import com.samwdev.battlecity.core.state.GameState
import com.samwdev.battlecity.core.state.MapState
import com.samwdev.battlecity.core.state.PowerUpState
import com.samwdev.battlecity.core.state.ScoreState
import com.samwdev.battlecity.core.state.TankState
import com.samwdev.battlecity.core.state.TickState
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class BattleViewModel(context: Application) : AndroidViewModel(context) {
    lateinit var battle: Battle
        private set

    private val _navFlow: MutableStateFlow<NavEvent?> = MutableStateFlow(null)
    val navFlow: StateFlow<NavEvent?> = _navFlow.asStateFlow()

    private var tickerJob: Job? = null
    var paused: Boolean = false

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

    var currentUIStatus: UIStatus by mutableStateOf(StageCurtain)
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
        showWaypoints = false,
    ))

    fun loadStage(stageName: String, startOver: Boolean = false) {
        currentUIStatus = StageCurtain
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
        battle.plugInDebugConfig(debugConfig)
        if (startOver) {
            gameState.resetGameState()
        }

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
        currentUIStatus = InGame
        gameState.gameStarted = true

        tickerJob = viewModelScope.launch {
            battle.startBattle()
        }
    }

    fun setUiStatusToTransitionToScoreboard() {
        currentUIStatus = TransitionToScoreboard
    }

    fun showScoreboard() {
        tickerJob?.cancel()
        currentUIStatus = ScoreboardDisplay
        navigate(NavEvent.Up)
        navigate(NavEvent.Scoreboard)
    }

    fun scoreboardCompleted() {
        when (requireNotNull(gameState.lastBattleResult)) {
            BattleResult.Won -> {
                currentUIStatus = StageCurtain
                goToNextStage()
            }
            BattleResult.Lost -> {
                navigate(NavEvent.GameOver)
            }
        }
    }

    fun resume() {
        SoundPlayer.INSTANCE.play(SoundEffect.Pause)
        battle.resume()
    }

    fun pause() {
        SoundPlayer.INSTANCE.play(SoundEffect.Pause)
        battle.pause()
    }

    fun exit() {
        tickerJob?.cancel()
    }
}

sealed class UIStatus(val index: Int) {
    operator fun compareTo(other: UIStatus) = index - other.index
}
object StageCurtain : UIStatus(1)
object InGame : UIStatus(4)
object TransitionToScoreboard : UIStatus(5)
object ScoreboardDisplay : UIStatus(10)

enum class BattleResult {
    Won, Lost
}