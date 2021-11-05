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
        if (handheldControllerState.direction != Direction.Unspecified) {
            val move = tank.speed * tick.delta
            tankState.moveTank(_tankId, handheldControllerState.direction, move)
        }

        if (handheldControllerState.firePressed) {

        }
    }
}