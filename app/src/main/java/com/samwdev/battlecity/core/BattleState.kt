package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun rememberBattleState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    tickState: TickState = rememberTickState(),
    tankState: TankState = rememberTankState(),
    bulletState: BulletState = rememberBulletState(),
    handheldControllerState: HandheldControllerState = rememberHandheldControllerState(),
    stageConfigJson: StageConfigJson,
): BattleState {
    val mapElements = remember(stageConfigJson) { MapParser.parse(stageConfigJson).map }
    val mapState = rememberMapState(mapElements = mapElements)
    return remember {
        BattleState(
            coroutineScope = coroutineScope,
            tickState = tickState,
            handheldControllerState = handheldControllerState,
            mapState = mapState,
            tankState = tankState,
            bulletState = bulletState,
        )
    }
}

class BattleState(
    private val coroutineScope: CoroutineScope,
    val tickState: TickState,
    val handheldControllerState: HandheldControllerState,
    val mapState: MapState,
    val bulletState: BulletState,
    val tankState: TankState,
) {
    fun start() {
        coroutineScope.launch {
            tickState.start()
        }

        coroutineScope.launch {
            tickState.tickFlow.collect { tick ->
                val tank = tankState.tanks.values.first()
                val move = tank.speed * tick.delta
                when (handheldControllerState.direction) {
                    Direction.Left -> tank.x -= move
                    Direction.Right -> tank.x += move
                    Direction.Up -> tank.y -= move
                    Direction.Down -> tank.y += move
                    Direction.Unspecified -> {}
                }
                if (handheldControllerState.direction != Direction.Unspecified) {
                    tank.direction = handheldControllerState.direction
                }

                if (handheldControllerState.firePressed) {

                }
            }
        }

    }
}
