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

    override fun onTick(tick: Tick) {
        if (bots.size < maxBot) {
            spawnBot()
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

    private fun spawnBot() {
        // todo tank level
        // todo there's a couple seconds delay to spawn
        val botTank = tankState.spawnBot(TankLevel.values().random(), carryPowerUp())
        bots = bots.toMutableMap().apply {
            put(botTank.id, AiTankController(
                tankId = botTank.id,
                tankState = tankState,
                bulletState = bulletState,
                mapState = mapState,
            ))
        }
    }

    private fun carryPowerUp(): Boolean {
        return Random.nextFloat() < 5
    }
}