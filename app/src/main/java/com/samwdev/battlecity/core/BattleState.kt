package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.ui.components.*
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun rememberBattleState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    tickState: TickState = rememberTickState(),
    tank: TankState = remember { TankState(0, 200) },
    controllerState: ControllerState = rememberControllerState(),
    stageConfigJson: StageConfigJson,
): BattleState {
    val mapElements = remember(stageConfigJson) { MapParser.parse(stageConfigJson).map }
    val mapState = rememberMapState(mapElements = mapElements)
    return remember {
        BattleState(
            coroutineScope = coroutineScope,
            tickState = tickState,
            controllerState = controllerState,
            mapState = mapState,
            tankState = tank
        )
    }
}

class BattleState(
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
                    Direction.Unspecified -> {}
                }
                if (controllerState.direction != Direction.Unspecified) {
                    tankState.direction = controllerState.direction
                }
            }
        }

    }
}
