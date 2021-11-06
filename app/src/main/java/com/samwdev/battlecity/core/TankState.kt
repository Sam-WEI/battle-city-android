package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.entity.BotTankLevel
import com.samwdev.battlecity.ui.components.mu

@Composable
fun rememberTankState(): TankState {
    return rememberSaveable(saver = TankState.Saver()) {
        TankState()
    }
}

private val playerSpawnPosition = Offset(4.5f, 12f)

class TankState(initial: Map<Int, Tank> = mapOf()) {
    companion object {
        // todo to confirm this works as expected
        fun Saver() = Saver<TankState, Map<Int, Tank>>(
            save = { it.tanks },
            restore = { TankState(it) }
        )

    }
    var tanks by mutableStateOf<Map<Int, Tank>>(initial, policy = referentialEqualityPolicy())
        private set

    private var nextId by mutableStateOf(1)

    private fun addTank(id: Int, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    fun spawnPlayer(): Tank {
        return Tank(
            x = playerSpawnPosition.x,
            y = playerSpawnPosition.y,
            direction = Direction.Up,
        ).also { addTank(nextId++, it) }
    }

    fun getTank(id: Int): Tank? {
        return tanks[id]
    }
}

class Tank(
    x: Float = 0f,
    y: Float = 0f,
    direction: Direction = Direction.Up,
    level: BotTankLevel = BotTankLevel.Basic,
    hp: Int = 1,
    val speed: Float = 0.01f,
) {
    var x: Float by mutableStateOf(x)
    var y: Float by mutableStateOf(y)
    var direction: Direction by mutableStateOf(direction)

    fun getBulletStartPosition(): DpOffset {
        return DpOffset.Zero
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
            .size(1.mu, 1.mu)
            .offset(tank.x.mu, tank.y.mu)
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