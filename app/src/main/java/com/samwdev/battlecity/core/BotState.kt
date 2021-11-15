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
        bots.values.forEach { controller ->
            controller.onTick(tick)
        }
    }

    fun spawnBot() {
        val bot = tankState.spawnBot()
        bots = bots.toMutableMap().apply {
            put(bot.id, BotTankController(tankState = tankState, tankId = bot.id, bulletState = bulletState))
        }
    }
}