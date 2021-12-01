package com.samwdev.battlecity.core

import androidx.compose.runtime.*

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

    private var botIndex = 0
    private val spawnDelay: Int get() = mapState.mapDifficulty.spawnDelay
    private var remainingSpawnDelay: Int = 0

    private val botQueue: List<TankLevel> = mapState.botGroups.flatMap {
        grp -> (0 until grp.count).map { grp.level }
    }

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
        val botTank = tankState.spawnBot(botQueue[botIndex % botQueue.size], carryPowerUp()) ?: return false // todo remove mod
        botIndex++
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
        return (botIndex + 4) % 7 == 0 // 4th, 11th, 18th, etc
    }
}