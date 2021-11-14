package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.samwdev.battlecity.utils.logI

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
        val tank = tankState.getTank(_tankId) ?: return
        val move = tank.speed * tick.delta
        var newX = tank.x
        var newY = tank.y
        var newDir = tank.direction
        when (handheldControllerState.direction) {
            Direction.Left -> newX -= move
            Direction.Right -> newX += move
            Direction.Up -> newY -= move
            Direction.Down -> newY += move
            Direction.Unspecified -> {}
        }
        if (handheldControllerState.direction != Direction.Unspecified) {
            newDir = handheldControllerState.direction
        }
        val newTank = tank.copy(x = newX, y = newY, direction = newDir)

        tankState.updateTank(_tankId, newTank)

        if (handheldControllerState.firePressed) {
            bulletState.addBullet(newTank)
        }
    }
}