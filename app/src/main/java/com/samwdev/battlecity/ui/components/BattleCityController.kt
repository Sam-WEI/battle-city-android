package com.samwdev.battlecity.ui.components

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.core.Direction
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun battleCityController(): ControllerState {
    return remember { ControllerState() }
}

class ControllerState {
    var direction: Direction = Direction.Unspecified
        private set
    suspend fun setCurrentInput(offset: Offset) {
        direction = getDirection(offset)
    }
}

private fun getDirection(steerOffset: Offset): Direction {
    val (x, y) = steerOffset
    val angle = atan2(y, x).toDouble()
    return when {
        angle < -PI / 4 && angle > -PI * 3 / 4 -> Direction.Up
        angle < -PI * 3 / 4 || angle > PI * 3 / 4 -> Direction.Left
        angle > PI / 4 && angle < PI * 3 / 4 -> Direction.Down
        angle > -PI / 4 && angle < 0 || angle < PI / 4 && angle > 0 -> Direction.Right
        else -> Direction.Unspecified
    }
}