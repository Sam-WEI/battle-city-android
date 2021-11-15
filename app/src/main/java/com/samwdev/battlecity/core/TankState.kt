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
import com.samwdev.battlecity.entity.BotTankLevel
import com.samwdev.battlecity.ui.components.mpx2dp
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun rememberTankState(): TankState {
    return rememberSaveable(saver = TankState.Saver()) {
        TankState()
    }
}

private val playerSpawnPosition = Offset(4.5f.grid2mpx, 12f.grid2mpx)

class TankState(initial: Map<TankId, Tank> = mapOf()) : TickListener {
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

    fun spawnBot(): Tank {
        return Tank(
            id = nextId.incrementAndGet(),
            x = 0f,
            y = 4f.grid2mpx,
            direction = Direction.Right,
            side = TankSide.Bot,
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

    fun startCooldown(id: TankId) {
        val tank = tanks[id]!!
        updateTank(id, tank.copy(remainingCooldown = tank.getFireCooldown()))
    }

    override fun onTick(tick: Tick) {
        tanks = tanks.keys.associateWith { id ->
            val tank = tanks[id]!!
            return@associateWith if (tank.remainingCooldown > 0) {
                tank.copy(remainingCooldown = tank.remainingCooldown - tick.delta.toInt())
            } else {
                tank
            }
        }
    }
}

typealias TankId = Int

@Parcelize
data class Tank(
    val id: TankId,
    val x: MapPixel = 0f,
    val y: MapPixel = 0f,
    val remainingCooldown: Int = 0,
    val direction: Direction = Direction.Up,
    val level: BotTankLevel = BotTankLevel.Basic,
    val hp: Int = 1,
    val speed: MapPixel = 0.15f,
    val side: TankSide = TankSide.Player,
) : Parcelable {
//    var x: Float by mutableStateOf(x)
//    var y: Float by mutableStateOf(y)
//    var direction: Direction by mutableStateOf(direction)

    fun getBulletStartPosition(): Offset {
        return when (direction) {
            Direction.Up -> Offset(x + 6, y)
            Direction.Down -> Offset(x + 6, y + 1.grid2mpx)
            Direction.Left -> Offset(x , y + 6)
            Direction.Right -> Offset(x + 1.grid2mpx, y + 6)
            Direction.Unspecified -> throw IllegalStateException()
        }
    }

    fun getFireCooldown(): Int = level.fireCooldown

    fun getMaxBulletLimit(): Int = 3
}

enum class Direction(val degree: Float) {
    Up(0f),
    Down(180f),
    Left(270f),
    Right(90f),
    Unspecified(Float.NaN)
}

enum class TankSide {
    Player,
    Bot,
}

@Composable
fun Tank(tank: Tank) {
    val color = if (tank.side == TankSide.Player) Color.Yellow else Color.LightGray
    Canvas(
        modifier = Modifier
            .size(TANK_MAP_PIXEL.mpx2dp, TANK_MAP_PIXEL.mpx2dp)
            .offset(tank.x.mpx2dp, tank.y.mpx2dp)
            .rotate(tank.direction.degree)
    ) {
        drawRect(
            color = color,
            topLeft = Offset(0f, size.height / 5f),
        )
        drawRect(
            color = color,
            topLeft = Offset(size.width / 2 - 24 / 2, 0f),
            size = Size(24f, size.height / 2)
        )
    }
}