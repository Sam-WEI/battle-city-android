package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TankController(
    private val tankState: TankState
) {
    private var _tankId: Int by mutableStateOf(-1)

    fun setTankId(id: Int) {
        _tankId = id
    }

    fun onTick() {
        val tank = tankState.tanks[_tankId]
        // todo check user input
    }
}