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
    soundState: SoundState = rememberSoundState(coroutine = coroutineScope),
    stageConfigJson: StageConfigJson,
    mapState: MapState = rememberMapState(mapElements = MapParser.parse(stageConfigJson).map),
    tickState: TickState = rememberTickState(),
    tankState: TankState = rememberTankState(),
    explosionState: ExplosionState = rememberExplosionState(),
    bulletState: BulletState = rememberBulletState(mapState = mapState, explosionState = explosionState, soundState = soundState),
    botState: BotState = rememberBotState(tankState = tankState, bulletState = bulletState),
    handheldControllerState: HandheldControllerState = rememberHandheldControllerState(),
): BattleState {
    return remember {
        BattleState(
            coroutineScope = coroutineScope,
            soundState = soundState,
            tickState = tickState,
            handheldControllerState = handheldControllerState,
            mapState = mapState,
            tankState = tankState,
            explosionState = explosionState,
            bulletState = bulletState,
            tankController = TankController(tankState, bulletState, handheldControllerState),
            botState = botState,
        )
    }
}

class BattleState(
    private val coroutineScope: CoroutineScope,
    val soundState: SoundState,
    val tickState: TickState,
    val handheldControllerState: HandheldControllerState,
    val mapState: MapState,
    val explosionState: ExplosionState,
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
                soundState.onTick(tick)
                tankController.onTick(tick)
                bulletState.onTick(tick)
                tankState.onTick(tick)
                botState.onTick(tick)
                explosionState.onTick(tick)
            }
        }

        tankState.spawnPlayer()
    }

    fun spawnTank() {

    }

}
