package com.samwdev.battlecity.core.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.samwdev.battlecity.core.Battle
import com.samwdev.battlecity.core.BattleResult
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.InGame
import com.samwdev.battlecity.core.TankLevel
import com.samwdev.battlecity.core.TickListener
import com.samwdev.battlecity.core.Timer
import com.samwdev.battlecity.core.UIStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameState(
    private val battleViewModel: BattleViewModel, // todo remove dep on the ViewModel
) : TickListener() {
    companion object {
        private const val ScoreboardShowUpDelay = 3 * 1000
        private const val InitialLife = 1
    }

    var totalScore: Int by mutableIntStateOf(0)
        private set

    var gameStarted = false
        set(value) {
            field = value
            lastBattleResult = null
        }

    var lastBattleResult: BattleResult? by mutableStateOf(null)
        private set

    private var scoreboardDelayTimer: Timer = Timer(ScoreboardShowUpDelay)

    private val _inGameEventFlow = MutableStateFlow<UIStatus>(InGame)
    val inGameEventFlow: StateFlow<UIStatus> = _inGameEventFlow.asStateFlow()

    var player1: PlayerData by mutableStateOf(PlayerData(InitialLife, TankLevel.Level1, false))
        private set

    fun updateAfterBattle(lastBattle: Battle) {
        totalScore += lastBattle.scoreState.battleScore
        player1 = player1.copy(carriedOverLife = true)
    }

    fun addPlayerLife() {
        player1 = player1.copy(remainingLife = player1.remainingLife + 1)
    }

    fun deductPlayerLife(): Boolean {
        if (player1.carriedOverLife) {
            player1 = player1.copy(carriedOverLife = false)
            return true
        }
        if (player1.remainingLife <= 0) {
            setGameResult(BattleResult.Lost)
            return false
        }
        player1 = player1.copy(remainingLife = player1.remainingLife - 1, tankLevel = TankLevel.Level1)
        return true
    }

    fun levelUp() {
        player1 = player1.copy(tankLevel = player1.tankLevel.nextLevel)
    }

    fun setGameResult(battleResult: BattleResult) {
        if (lastBattleResult == null) {
            gameStarted = false
            lastBattleResult = battleResult
            updateAfterBattle(battleViewModel.battle)
            battleViewModel.setUiStatusToTransitionToScoreboard()
            // after map is cleared, wait 3 seconds before displaying scoreboard
            scoreboardDelayTimer.resetAndActivate()
        }
    }

    fun resetGameState() {
        totalScore = 0
        player1 = PlayerData(InitialLife, TankLevel.Level1, false)
    }

    override fun onTick(tick: Tick) {
        if (scoreboardDelayTimer.isActive) {
            if (scoreboardDelayTimer.tick(tick)) {
                battleViewModel.showScoreboard()
            }
        }
    }
}

data class PlayerData(
    val remainingLife: Int,
    val tankLevel: TankLevel,
    val carriedOverLife: Boolean,
)