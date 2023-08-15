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
) {

    private val _inGameEventFlow = MutableStateFlow<GameStatus>(Playing)
    val inGameEventFlow: StateFlow<GameStatus> = _inGameEventFlow.asStateFlow()

    var totalScore: Int by mutableIntStateOf(0)
        private set

    var player1: PlayerData by mutableStateOf(PlayerData(3, TankLevel.Level1))
        private set

    fun update(lastBattleState: BattleState) {
        totalScore += lastBattleState.scoreState.generateScoreboardData().totalScore
    }

    fun addPlayerLife() {
        player1 = player1.copy(remainingLife = player1.remainingLife + 1)
    }

    fun deductPlayerLife(): Boolean {
        if (player1.remainingLife == 0) {
            _inGameEventFlow.value = GameOver
            return false
        }
        player1 = player1.copy(remainingLife = player1.remainingLife - 1, tankLevel = TankLevel.Level1)
        return true
    }

    fun levelUp() {
        player1 = player1.copy(tankLevel = player1.tankLevel.nextLevel)
    }

    fun mapCleared() {

    }

    fun gameOver() {
        _inGameEventFlow.value = GameOver
    }
}

data class PlayerData(
    val remainingLife: Int,
    val tankLevel: TankLevel,
)