package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.PowerUp
import kotlin.random.Random

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
    var maxBot: Int = 1
    var bots by mutableStateOf<Map<TankId, BotTankController>>(mapOf())

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
        val botTank = tankState.spawnBot(TankLevel.values().random(), Random.nextFloat().let { if (it < 0.2) PowerUp.Star else null })
        bots = bots.toMutableMap().apply {
            put(botTank.id, BotTankController(tankState = tankState, tankId = botTank.id, bulletState = bulletState))
        }
    }
}