package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.PowerUpEnum
import java.util.concurrent.atomic.AtomicInteger

typealias PowerUpId = Int

class PowerUpState(
    private val mapState: MapState,
) {
    private val idGen: AtomicInteger = AtomicInteger(0)
    var maxPowerUp: Int = 1
    var powerUps by mutableStateOf<Map<PowerUpId, PowerUp>>(mapOf())
        private set

    fun spawnPowerUp() {
        trimOldPowerUps()
        powerUps = powerUps.toMutableMap().apply {
            val pos = mapState.accessPoints.randomAccessiblePoint(5)
            put(idGen.incrementAndGet(), PowerUp(idGen.get(), pos.x, pos.y, randomPowerUp()))
        }
    }

    fun remove(pickedUp: List<PowerUp>) {
        val set = pickedUp.map { it.id }.toSet()
        powerUps = powerUps.filter { it.key !in set }
    }

    private fun trimOldPowerUps() {
        if (powerUps.size > maxPowerUp - 1) {
            val mutable = powerUps.toSortedMap()
            val iterator = mutable.iterator()
            repeat (powerUps.size - (maxPowerUp - 1)) {
                iterator.next()
                iterator.remove()
            }
            powerUps = mutable
        }
    }

    private fun randomPowerUp(): PowerUpEnum {
        return PowerUpEnum.values().random()
    }
}

data class PowerUp(
    val id: PowerUpId,
    val x: MapPixel,
    val y: MapPixel,
    val type: PowerUpEnum,
) {
    val rect: Rect get() = Rect(offset, Size(1f.cell2mpx, 1f.cell2mpx))
    val offset: Offset get() = Offset(x, y)
}