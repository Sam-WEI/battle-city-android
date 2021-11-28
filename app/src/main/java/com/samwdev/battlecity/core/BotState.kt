package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import kotlin.random.Random

@Composable
fun rememberBotState(
    tankState: TankState,
    bulletState: BulletState,
    mapState: MapState,
) = remember {
    BotState(tankState = tankState, bulletState = bulletState, mapState = mapState)
}

class BotState(
    private val tankState: TankState,
    private val bulletState: BulletState,
    private val mapState: MapState,
) : TickListener {
    var maxBot: Int = 1
    var bots: Map<TankId, AiTankController> by mutableStateOf(mapOf())

    private var botSpawnedSoFar = 0
    private val spawnDelay: Int get() = 1000 // todo based on map difficulty, 3000, 2000, 1000
    private var remainingSpawnDelay: Int = 0

    override fun onTick(tick: Tick) {
        if (bots.size < maxBot) {
            remainingSpawnDelay -= tick.delta // only count down when short of bots
            if (remainingSpawnDelay < 0) {
                if (spawnBot()) {
                    remainingSpawnDelay = spawnDelay
                }
            }
        } else if (bots.size > maxBot) {
            bots.entries.take(bots.size - maxBot).forEach {
                removeBot(it.key)
            }
        }
        clearDeadBots()
        bots.values.forEach { controller ->
            controller.onTick(tick)
        }
    }

    private fun clearDeadBots() {
        bots = bots.filter { tankState.isTankAlive(it.key) }
    }

    private fun removeBot(tankId: TankId) {
        bots = bots.toMutableMap().apply {
            remove(tankId)
        }
        tankState.killTank(tankId)
    }

    /** Return false if failed to spawn a bot */
    private fun spawnBot(): Boolean {
        // todo tank level
        val botTank = tankState.spawnBot(TankLevel.values().random(), carryPowerUp()) ?: return false
        botSpawnedSoFar++
        bots = bots.toMutableMap().apply {
            put(botTank.id, AiTankController(
                tankId = botTank.id,
                tankState = tankState,
                bulletState = bulletState,
                mapState = mapState,
            ))
        }
        return true
    }

    private fun carryPowerUp(): Boolean {
        return (botSpawnedSoFar + 1) % 5 == 0
    }
}