package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TankController(
    private val tankState: TankState,
    private val handheldControllerState: HandheldControllerState,
) : TickListener {
    private var _tankId: Int by mutableStateOf(-1)

    fun setTankId(id: Int) {
        _tankId = id
    }

    override fun onTick(tick: Tick) {
        val tank = tankState.getTank(_tankId) ?: return
        val move = tank.speed * tick.delta
        when (handheldControllerState.direction) {
            Direction.Left -> tank.x -= move
            Direction.Right -> tank.x += move
            Direction.Up -> tank.y -= move
            Direction.Down -> tank.y += move
            Direction.Unspecified -> {}
        }
        if (handheldControllerState.direction != Direction.Unspecified) {
            tank.direction = handheldControllerState.direction
        }

        if (handheldControllerState.firePressed) {

        }
    }
}