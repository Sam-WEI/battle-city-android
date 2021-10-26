package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.ui.components.ControllerState
import com.samwdev.battlecity.ui.components.TankState
import com.samwdev.battlecity.ui.components.rememberControllerState

@Composable
fun rememberGameState(
    tickState: TickState = rememberTickState(),
    tank: TankState = remember { TankState(0, 200) },
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
