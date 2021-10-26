package com.samwdev.battlecity.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.core.Direction
import kotlin.math.atan2

@Composable
fun rememberControllerState(): ControllerState {
    return remember { ControllerState() }
}

class ControllerState {
    var direction by mutableStateOf(Direction.Unspecified)
        private set

    fun setCurrentInput(offset: Offset) {
        direction = getDirection(offset)
    }
}

private fun getDirection(steerOffset: Offset): Direction {
    val (x, y) = steerOffset
    val angle = Math.toDegrees(atan2(y, x).toDouble())

    return when {
        x == 0f && y == 0f -> Direction.Unspecified
        angle <= -45 && angle > -135 -> Direction.Up
        angle <= -135 || angle > 135 -> Direction.Left
        angle <= 135 && angle > 45 -> Direction.Down
        angle <= 45 && angle > 0 || angle > -45 && angle <= 0 -> Direction.Right
        else -> Direction.Unspecified
    }
}