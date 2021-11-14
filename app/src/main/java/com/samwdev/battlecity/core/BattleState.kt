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
            tankController = TankController(tankState, bulletState, handheldControllerState),
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
    val tankController: TankController,
) {
    fun startBattle() {
        coroutineScope.launch {
            tickState.start()
        }
        tankController.setTankId(1)
        coroutineScope.launch {
            tickState.tickFlow.collect { tick ->
                tankController.onTick(tick)
                bulletState.onTick(tick)
            }
        }

        tankState.spawnPlayer()
    }

    fun spawnTank() {

    }

}
