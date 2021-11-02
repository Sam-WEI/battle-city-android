package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.MapElements
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.ui.components.*
import com.samwdev.battlecity.utils.MapParser
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
    stageConfigJson: StageConfigJson,
): GameState {
    val mapState = rememberMapState(mapElements = MapParser.parse(stageConfigJson).map)
    return remember {
        GameState(
            coroutineScope,
            tickState = tickState,
            controllerState = controllerState,
            mapState = mapState,
            tankState = tank
        )
    }
}

class GameState(
    private val coroutineScope: CoroutineScope,
    val tickState: TickState,
    val controllerState: ControllerState,
    val mapState: MapState,
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
