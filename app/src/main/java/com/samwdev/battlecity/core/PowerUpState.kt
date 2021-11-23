package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.PowerUpEnum
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

typealias PowerUpId = Int

@Composable
fun rememberPowerUpState(
    mapState: MapState
) = remember { PowerUpState(mapState) }

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
            val pos = getAvailablePosition()
            put(idGen.incrementAndGet(), PowerUp(idGen.get(), pos.x, pos.y, randomPowerUp()))
        }
    }

    fun remove(pickedUp: List<PowerUp>) {
        val set = pickedUp.map { it.id }.toSet()
        powerUps = powerUps.toMutableMap().filter { it.key !in set }
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

    private fun getAvailablePosition(): Offset {
        // todo
        return Offset(
            Random.nextInt(0, (MAP_BLOCK_COUNT - 1)).grid2mpx,
            Random.nextInt(0, (MAP_BLOCK_COUNT - 1)).grid2mpx
        )
    }

    private fun randomPowerUp(): PowerUpEnum {
        // todo
        return PowerUpEnum.values().random()
    }
}

data class PowerUp(
    val id: PowerUpId,
    val x: MapPixel,
    val y: MapPixel,
    val type: PowerUpEnum,
) {
    val rect: Rect get() = Rect(Offset(x, y), Size(1f.grid2mpx, 1f.grid2mpx))
}