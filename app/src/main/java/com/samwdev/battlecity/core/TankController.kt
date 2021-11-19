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
        var tank = tankState.getTankOrNull(_tankId) ?: return
        if (tank.isSpawning) {
            return
        }
        val distance = tank.speed * tick.delta

        val newDir = handheldControllerState.direction
        tank = if (newDir != null) {
            tank.move(distance = distance, turnTo = newDir)
        } else {
            tank.copy(isMoving = false)
        }
        tankState.updateTank(_tankId, tank)

        if (handheldControllerState.firePressed && tank.remainingCooldown <= 0) {
            if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
                bulletState.fire(tank)
                tankState.startCooldown(_tankId)
            }
        }
    }
}