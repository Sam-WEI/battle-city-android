package com.samwdev.battlecity.core

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

typealias ScoreId = Int

class ScoreState : TickListener() {
    var battleScore: Int = 0
        private set
    private val tankKillCount = mutableMapOf<TankLevel, Int>()

    private val idGen: AtomicInteger = AtomicInteger(0)

    private var onScreenScores by mutableStateOf(mapOf<ScoreId, OnScreenScore>())

    val onShowScreenScores by derivedStateOf { onScreenScores.filter { it.value.remainingDelay <= 0 } }

    override fun onTick(tick: Tick) {
        onScreenScores = onScreenScores.filter { (_, score) -> score.remainingOnScreenTime > 0 }
            .mapValues { (_, score) ->
                if (score.remainingDelay > 0) {
                    score.copy(remainingDelay = score.remainingDelay - tick.delta)
                } else {
                    score.copy(remainingOnScreenTime = score.remainingOnScreenTime - tick.delta)
                }
            }
    }

    fun kill(level: TankLevel, offset: Offset) {
        tankKillCount[level] = (tankKillCount[level] ?: 0) + 1
        addNewOnScreenScore(OnScreenScore(
            id = idGen.get(),
            offset = offset,
            remainingDelay = 500,
            remainingOnScreenTime = 500,
            score = level.killScore,
        ))
    }

    fun pickUpPowerUp(offset: Offset) {
        addNewOnScreenScore(OnScreenScore(
            id = idGen.get(),
            offset = offset,
            remainingDelay = 0,
            remainingOnScreenTime = 1000,
            score = 500,
        ))
    }

    fun collectScoreboardData(): ScoreboardData {
        return ScoreboardData(
            battleScore,
            tankKillCount.apply {
                TankLevel.values().forEach { getOrPut(it) { 0 } }
            }.toSortedMap()
        )
    }

    private fun addNewOnScreenScore(onScreenScore: OnScreenScore) {
        onScreenScores = onScreenScores.toMutableMap().apply {
            put(idGen.incrementAndGet(), onScreenScore)
        }
        battleScore += onScreenScore.score
    }
}

data class ScoreboardData(
    val totalScore: Int,
    val killCount: Map<TankLevel, Int>,
) : Serializable

data class OnScreenScore(
    val id: ScoreId,
    val offset: Offset,
    val score: Int,
    val remainingDelay: Int,
    val remainingOnScreenTime: Int,
)