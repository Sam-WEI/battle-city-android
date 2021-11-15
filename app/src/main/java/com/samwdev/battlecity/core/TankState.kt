package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.entity.BotTankLevel
import com.samwdev.battlecity.entity.MAP_PIXEL_IN_EACH_BLOCK
import com.samwdev.battlecity.ui.components.mpDp
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger

const val TANK_MAP_PIXEL = MAP_PIXEL_IN_EACH_BLOCK

@Composable
fun rememberTankState(): TankState {
    return rememberSaveable(saver = TankState.Saver()) {
        TankState()
    }
}

private val playerSpawnPosition = Offset(4.5f, 12f)

class TankState(initial: Map<TankId, Tank> = mapOf()) {
    companion object {
        // todo to confirm this works as expected
        fun Saver() = Saver<TankState, Map<TankId, Tank>>(
            save = { it.tanks },
            restore = { TankState(it) }
        )

    }
    var tanks by mutableStateOf<Map<TankId, Tank>>(initial)
        private set

    private var nextId = AtomicInteger(0)

    private fun addTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    fun spawnPlayer(): Tank {
        return Tank(
            id = nextId.incrementAndGet(),
            x = playerSpawnPosition.x,
            y = playerSpawnPosition.y,
            direction = Direction.Up,
        ).also { addTank(nextId.get(), it) }
    }

    fun getTank(id: TankId): Tank? {
        return tanks[id]
    }

    fun updateTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }
}

typealias TankId = Int

@Parcelize
data class Tank(
    val id: TankId,
    val x: Float = 0f,
    val y: Float = 0f,
    val direction: Direction = Direction.Up,
    val level: BotTankLevel = BotTankLevel.Basic,
    val hp: Int = 1,
    val speed: Float = 0.06f,
) : Parcelable {
//    var x: Float by mutableStateOf(x)
//    var y: Float by mutableStateOf(y)
//    var direction: Direction by mutableStateOf(direction)

    fun getBulletStartPosition(): DpOffset {
        return DpOffset(x.dp + 0.5f.dp, y.dp + 0.5f.dp)
    }
}

enum class Direction(val degree: Float) {
    Up(0f),
    Down(180f),
    Left(270f),
    Right(90f),
    Unspecified(Float.NaN)
}

@Composable
fun Tank(tank: Tank) {
    Canvas(
        modifier = Modifier
            .size(TANK_MAP_PIXEL.mpDp, TANK_MAP_PIXEL.mpDp)
            .offset(tank.x.mpDp, tank.y.mpDp)
            .rotate(tank.direction.degree)
    ) {
        drawRect(
            color = Color.Yellow,
            topLeft = Offset(0f, size.height / 5f),
//            alpha = (tickState.uptimeMillis % 1500) / 3000f + 0.5f
        )
        drawRect(
            color = Color.Yellow,
            topLeft = Offset(size.width / 2 - 24 / 2, 0f),
            size = Size(24f, size.height / 2)
        )
    }
}