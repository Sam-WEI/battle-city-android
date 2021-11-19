package com.samwdev.battlecity.core

import androidx.compose.runtime.*

@Composable
fun rememberBotState(
    tankState: TankState,
    bulletState: BulletState,
) = remember {
    BotState(tankState = tankState, bulletState = bulletState)
}

class BotState(
    private val tankState: TankState,
    private val bulletState: BulletState,
) : TickListener {
    var bots by mutableStateOf<Map<TankId, BotTankController>>(mapOf())

    override fun onTick(tick: Tick) {
        if (bots.size < 1) {
            spawnBot()
        }
        clearDeadBots()
        bots.values.forEach { controller ->
            controller.onTick(tick)
        }
    }

    fun clearDeadBots() {
        bots = bots.filter { tankState.isTankAlive(it.key) }
    }

    fun removeBot(tankId: TankId) {
        bots = bots.toMutableMap().apply {
            remove(tankId)
        }
        tankState.killTank(tankId)
    }

    fun spawnBot() {
        val botTank = tankState.spawnBot()
        bots = bots.toMutableMap().apply {
            put(botTank.id, BotTankController(tankState = tankState, tank = botTank, bulletState = bulletState))
        }
    }
}