package com.samwdev.battlecity.ui.components

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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

class TankState(
    x: Int = 0,
    y: Int = 0,
    direction: Direction = Direction.Up,
    val speed: Float = 0.4f,
) {
    var x: Int by mutableStateOf(x)
    var y: Int by mutableStateOf(y)
    var direction: Direction by mutableStateOf(direction)
}

enum class Direction(val degree: Float) {
    Up(0f), Down(180f), Left(270f), Right(90f), Unspecified(Float.NaN)
}

@Composable
fun Tank(tank: TankState) {
    Canvas(
        modifier = Modifier.size(50.dp, 50.dp)
            .offset { IntOffset(tank.x, tank.y) }
            .rotate(tank.direction.degree)
    ) {
        drawRect(
            color = Color.Black,
            topLeft = Offset(0f, size.height / 5f),
//            alpha = (tickState.uptimeMillis % 1500) / 3000f + 0.5f
        )
        drawRect(
            color = Color.DarkGray,
            topLeft = Offset(size.width / 2 - 24 / 2, 0f),
            size = Size(24f, size.height / 2)
        )
    }
}