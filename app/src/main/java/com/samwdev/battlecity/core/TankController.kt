package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TankController(
    private val tankState: TankState,
    private val bulletState: BulletState, // todo move out?
    private val handheldControllerState: HandheldControllerState,
) : TickListener {
    private var _tankId: Int by mutableStateOf(-1)

    fun setTankId(id: Int) {
        _tankId = id
    }

    override fun onTick(tick: Tick) {
        val tank = tankState.getTankOrNull(_tankId) ?: return
        if (tank.isSpawning) {
            return
        }
        val distance = tank.speed * tick.delta

        val newDir = handheldControllerState.direction
        if (newDir != null) {
            tankState.moveTank(_tankId, newDir, distance)
        } else {
            tankState.stopTank(_tankId)
        }

        if (handheldControllerState.firePressed && tank.remainingCooldown <= 0) {
            if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
                bulletState.fire(tank)
                tankState.startCooldown(_tankId)
            }
        }
    }
}