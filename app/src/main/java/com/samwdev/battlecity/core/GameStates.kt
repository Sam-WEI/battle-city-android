package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.ui.components.ControllerState
import com.samwdev.battlecity.ui.components.Direction
import com.samwdev.battlecity.ui.components.TankState
import com.samwdev.battlecity.ui.components.rememberControllerState
import com.samwdev.battlecity.utils.logE
import com.samwdev.battlecity.utils.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun rememberGameState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    tickState: TickState = rememberTickState(),
    tank: TankState = remember { TankState(0, 200) },
    controllerState: ControllerState = rememberControllerState(),
) = remember {
    GameState(
        coroutineScope,
        tickState = tickState,
        controllerState = controllerState,
        tankState = tank
    )
}

class GameState(
    private val coroutineScope: CoroutineScope,
    val tickState: TickState,
    val controllerState: ControllerState,
    val tanks: Map<String, TankState> = mapOf(),
    val tankState: TankState,
) {
    fun start() {
        coroutineScope.launch {
            tickState.start()
        }

        coroutineScope.launch {
            tickState.tickFlow.collect { tick ->
                val move = (tankState.speed * tick.delta).roundToInt()
                when (controllerState.direction) {
                    Direction.Left -> tankState.x -= move
                    Direction.Right -> tankState.x += move
                    Direction.Up -> tankState.y -= move
                    Direction.Down -> tankState.y += move
                }
                if (controllerState.direction != Direction.Unspecified) {
                    tankState.direction = controllerState.direction
                }
            }
        }

    }
}
