package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.ui.components.mu

class Tank(
    x: Float = 0f,
    y: Float = 0f,
    direction: Direction = Direction.Up,
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
    Up(0f), Down(180f), Left(270f), Right(90f), Unspecified(Float.NaN)
}

@Composable
fun Tank(tank: Tank) {
    Canvas(
        modifier = Modifier.size(1.mu, 1.mu)
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