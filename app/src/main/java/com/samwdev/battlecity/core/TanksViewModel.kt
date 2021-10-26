package com.samwdev.battlecity.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.utils.logI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TanksViewModel : ViewModel() {
    private val _tanks = MutableStateFlow(
        mutableMapOf("player-1" to TankState(x = 100, y = 100))
    )

    private val _tank = MutableStateFlow(
        TankState(x = 0, y = 0)
    )

    val tanks: StateFlow<Map<String, TankState>> = _tanks
    val tank: StateFlow<TankState> = _tank

    fun updateTank(id: String, tank: TankState) {
        _tanks.value[id] = tank
    }

    fun updateTank(id: String, dx: Int, dy: Int) {
        val oldTanks = _tanks.value
        val old = oldTanks[id]!!
//        oldTanks[id] = old.copy(x = old.x + dx, y = old.y + dy)
        _tanks.tryEmit(oldTanks)
    }

    fun updateTank(dx: Int, dy: Int) {
        val old = _tank.value
//        _tank.tryEmit(old.copy(x = old.x + dx, y = old.y + dy))
    }
}