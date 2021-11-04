package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun rememberBattleState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    tickState: TickState = rememberTickState(),
    tank: Tank = remember { Tank(0f, 0f) },
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
            tank = tank
        )
    }
}

class BattleState(
    private val coroutineScope: CoroutineScope,
    val tickState: TickState,
    val controllerState: ControllerState,
    val mapState: MapState,
    val tanks: Map<String, Tank> = mapOf(),
    val tank: Tank,
) {
    fun start() {
        coroutineScope.launch {
            tickState.start()
        }

        coroutineScope.launch {
            tickState.tickFlow.collect { tick ->
                val move = tank.speed * tick.delta
                when (controllerState.direction) {
                    Direction.Left -> tank.x -= move
                    Direction.Right -> tank.x += move
                    Direction.Up -> tank.y -= move
                    Direction.Down -> tank.y += move
                    Direction.Unspecified -> {}
                }
                if (controllerState.direction != Direction.Unspecified) {
                    tank.direction = controllerState.direction
                }

                if (controllerState.firePressed) {

                }
            }
        }

    }
}
