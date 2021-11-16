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
    stageConfigJson: StageConfigJson,
    mapState: MapState = rememberMapState(mapElements = MapParser.parse(stageConfigJson).map),
    tickState: TickState = rememberTickState(),
    tankState: TankState = rememberTankState(),
    bulletState: BulletState = rememberBulletState(mapState = mapState),
    botState: BotState = rememberBotState(tankState = tankState, bulletState = bulletState),
    handheldControllerState: HandheldControllerState = rememberHandheldControllerState(),
): BattleState {
    return remember {
        BattleState(
            coroutineScope = coroutineScope,
            tickState = tickState,
            handheldControllerState = handheldControllerState,
            mapState = mapState,
            tankState = tankState,
            bulletState = bulletState,
            tankController = TankController(tankState, bulletState, handheldControllerState),
            botState = botState,
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
    val botState: BotState,
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
                tankState.onTick(tick)
                botState.onTick(tick)
            }
        }

        tankState.spawnPlayer()
    }

    fun spawnTank() {

    }

}
