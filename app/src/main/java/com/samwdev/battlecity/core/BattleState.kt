package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun rememberBattleState(
    stageConfig: StageConfig,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    soundState: SoundState = rememberSoundState(coroutine = coroutineScope),
    explosionState: ExplosionState = rememberExplosionState(),
    tickState: TickState = rememberTickState(),
    scoreState: ScoreState = ScoreState(),
    mapState: MapState = rememberMapState(stageConfig = stageConfig),
    powerUpState: PowerUpState = rememberPowerUpState(mapState = mapState),
    tankState: TankState = rememberTankState(
        explosionState = explosionState,
        soundState = soundState,
        mapState = mapState,
        powerUpState = powerUpState,
        scoreState = scoreState,
    ),
    bulletState: BulletState = rememberBulletState(
        mapState = mapState,
        tankState = tankState,
        explosionState = explosionState,
        soundState = soundState,
    ),
    botState: BotState = rememberBotState(
        tankState = tankState,
        bulletState = bulletState,
        mapState = mapState,
    ),
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
            powerUpState = powerUpState,
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
    val powerUpState: PowerUpState,
) {
    fun startBattle() {
        coroutineScope.launch {
            tickState.start()
        }
        coroutineScope.launch {
            tickState.tickFlow.collect { tick ->
                mapState.onTick(tick)
                soundState.onTick(tick)
                tankController.onTick(tick)
                bulletState.onTick(tick)
                botState.onTick(tick)
                tankState.onTick(tick)
                explosionState.onTick(tick)
            }
        }
    }
}
