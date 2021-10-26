package com.samwdev.battlecity.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.samwdev.battlecity.ui.components.ControllerState
import com.samwdev.battlecity.ui.components.rememberControllerState

@Composable
fun rememberGameState(
    tank: TankState = remember { TankState(0, 0) },
    controllerState: ControllerState = rememberControllerState(),
) = remember {
    GameState(
        controllerState = controllerState,
        tank = tank
    )
}

class GameState(
    val controllerState: ControllerState,
    val tanks: Map<String, TankState> = mapOf(),
    val tank: TankState,
) {
    
}

data class TankState(
    val x: Int,
    val y: Int,
    val direction: Direction = Direction.Up,
)

enum class Direction {
    Up, Down, Left, Right, Unspecified
}