package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BotTankController(
    private val tankState: TankState,
    private val tankId: TankId,
    private val bulletState: BulletState, // todo move out?
) : TickListener {
    private var _tankId: Int by mutableStateOf(-1)

    private val tank: Tank get() = tankState.getTank(tankId)!!
    private val botAi: BotAi = BotAi()

    override fun onTick(tick: Tick) {
        botAi.onTick(tick)
        val command = botAi.getCommand()
        val move = tank.speed * tick.delta
        var newX = tank.x
        var newY = tank.y
        var newDir = tank.direction
        val newTank = tank.copy(x = newX, y = newY, direction = newDir)

        tankState.updateTank(_tankId, newTank)

        when (command) {
            is Fire -> {
                if (tank.remainingCooldown <= 0) {
                    if (bulletState.countBulletForTank(tank.id) < tank.getMaxBulletLimit()) {
                        bulletState.fire(newTank)
                        tankState.startCooldown(newTank.id)
                    }
                }
            }
        }
    }
}