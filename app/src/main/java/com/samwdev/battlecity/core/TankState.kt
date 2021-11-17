package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.entity.BotTankLevel
import com.samwdev.battlecity.ui.components.LocalTick
import com.samwdev.battlecity.ui.components.Map
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

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
    val speed: MapPixel = 0.05f,
    val side: TankSide = TankSide.Player,
    val isMoving: Boolean = false,
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
        }
    }

    fun getFireCooldown(): Int = level.fireCooldown

    fun getMaxBulletLimit(): Int = if (side == TankSide.Player) 3 else 1
}

enum class Direction(val degree: Float) {
    Up(0f),
    Down(180f),
    Left(270f),
    Right(90f);

    fun isVertical(): Boolean = this == Up || this == Down
    fun isHorizontal(): Boolean = this == Left || this == Right
}

enum class TankSide {
    Player,
    Bot,
}

private const val TreadPatternCycleDistance = 10

@Composable
fun Tank(tank: Tank) {
    val color1 = Color(227, 227, 137)
    val color2 = Color(227, 145, 30)
    val color3 = Color(96, 96, 0)

    var travelDistance by remember { mutableStateOf(0) }
    var treadPattern: Int by remember { mutableStateOf(0) } // 0 or 1

    if (tank.isMoving) {
        // calculate tread pattern based on travel distance, so faster tank looks faster
        travelDistance += (tank.speed * LocalTick.current.delta).roundToInt()
        treadPattern = if (travelDistance % (TreadPatternCycleDistance * 2) < TreadPatternCycleDistance)
            0 else 1
    }

    PixelCanvas(
        heightInMapPixel = TANK_MAP_PIXEL,
        widthInMapPixel = TANK_MAP_PIXEL,
        topLeftInMapPixel = Offset(tank.x, tank.y) ,
        modifier = Modifier
            .rotate(tank.direction.degree)
    ) {
        // left tread
        translate(1f, 4f) {
            drawRect(
                color = color1,
                topLeft = Offset(0f, 0f),
                size = Size(3f, 11f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(1f, 0f),
                size = Size(2f, 11f),
            )
            drawRect(
                color = color1,
                topLeft = Offset(2f, 1f),
                size = Size(1f, 9f),
            )
            for (i in treadPattern..10 step 2) {
                val left = if (i == 0 || i == 10) 1 else 0
                drawRect(
                    color = color3,
                    topLeft = Offset(left.toFloat(), i.toFloat()),
                    size = Size(2f, 1f),
                )
            }
        }

        // right tread
        translate(left = 11f, top = 4f) {
            drawRect(
                color = color1,
                topLeft = Offset(0f, 0f),
                size = Size(1f, 1f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(0f, 1f),
                size = Size(1f, 10f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(1f, 0f),
                size = Size(2f, 11f),
            )
            for (i in treadPattern..10 step 2) {
                drawRect(
                    color = color3,
                    topLeft = Offset(1f, i.toFloat()),
                    size = Size(2f, 1f),
                )
            }
        }

        translate(4f, 6f) {
            // body
            drawRect(
                color = color1,
                topLeft = Offset(0f, 1f),
                size = Size(7f, 6f),
            )
            drawRect(
                color = color1,
                topLeft = Offset(1f, 0f),
                size = Size(5f, 8f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(2f, 0f),
                size = Size(4f, 2f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(4f, 2f),
                size = Size(3f, 4f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(3f, 3f),
                size = Size(3f, 4f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(1f, 2f),
                size = Size(1f, 3f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(2f, 5f),
                size = Size(1f, 1f),
            )
            // body shadow
            drawRect(
                color = color3,
                topLeft = Offset(4f, 0f),
                size = Size(2f, 1f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(6f, 1f),
                size = Size(1f, 1f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(4f, 3f),
                size = Size(1f, 3f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(3f, 5f),
                size = Size(1f, 1f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(0f, 6f),
                size = Size(1f, 1f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(6f, 6f),
                size = Size(1f, 1f),
            )
            drawRect(
                color = color3,
                topLeft = Offset(1f, 7f),
                size = Size(5f, 1f),
            )
        }

        // barrel
        drawRect(
            color = color1,
            topLeft = Offset(7f, 2f),
            size = Size(1f, 5f),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(1000.dp)) {
            Tank(Tank(
                id = 0,
            ))
        }
    }
}