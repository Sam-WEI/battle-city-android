package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.ui.components.ControllerState
import com.samwdev.battlecity.ui.components.rememberControllerState

@Composable
fun rememberGameState(
    tickState: TickState = rememberTickState(),
    tank: TankState = remember { TankState(0, 0) },
    controllerState: ControllerState = rememberControllerState(),
) = remember {
    GameState(
        tickState = tickState,
        controllerState = controllerState,
        tankState = tank
    )
}

class GameState(
    val tickState: TickState,
    val controllerState: ControllerState,
    val tanks: Map<String, TankState> = mapOf(),
    val tankState: TankState,
)

class TankState(
    x: Int = 0,
    y: Int = 0,
    direction: Direction = Direction.Up,
) {
    var x: Int by mutableStateOf(x)
    var y: Int by mutableStateOf(x)
    var direction: Direction by mutableStateOf(direction)
}

enum class Direction {
    Up, Down, Left, Right, Unspecified
}