package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameState(
    private val battleViewModel: BattleViewModel,
) : TickListener() {
    companion object {
        private const val ScoreboardShowUpDelay = 3 * 1000
    }

    var totalScore: Int by mutableIntStateOf(0)
        private set

    var gameStarted = false
        set(value) {
            field = value
            lastBattleResult = null
        }

    var lastBattleResult: BattleResult? = null
        private set

    private var scoreboardDelayTimer: Timer = Timer(ScoreboardShowUpDelay)

    private val _inGameEventFlow = MutableStateFlow<GameStatus>(InGame)
    val inGameEventFlow: StateFlow<GameStatus> = _inGameEventFlow.asStateFlow()

    var player1: PlayerData by mutableStateOf(PlayerData(3, TankLevel.Level1))
        private set

    fun updateAfterBattle(lastBattle: Battle) {
        totalScore += lastBattle.scoreState.battleScore
        addPlayerLife() // compensate the spawn cost for next map
    }

    fun addPlayerLife() {
        player1 = player1.copy(remainingLife = player1.remainingLife + 1)
    }

    fun deductPlayerLife(): Boolean {
        if (player1.remainingLife == 0) {
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

            // after map is cleared, wait 3 seconds before displaying scoreboard
            scoreboardDelayTimer.resetAndActivate()
        }
    }

    override fun onTick(tick: Tick) {
        if (scoreboardDelayTimer.isActive) {
            if (scoreboardDelayTimer.tick(tick)) {
                battleViewModel.setGameResult(BattleResult.Won)
            }
        }
    }
}

data class PlayerData(
    val remainingLife: Int,
    val tankLevel: TankLevel,
)