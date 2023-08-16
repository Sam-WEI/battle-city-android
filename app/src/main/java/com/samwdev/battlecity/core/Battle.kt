package com.samwdev.battlecity.core

import com.samwdev.battlecity.entity.StageConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Battle(
    gameState: GameState,
    stageConfig: StageConfig,
) {
    val tickState = TickState()
    val soundState = SoundState()
    val explosionState = ExplosionState()
    val handheldControllerState = HandheldControllerState()
    val scoreState = ScoreState()
    val mapState: MapState = MapState(gameState, stageConfig)
    val powerUpState: PowerUpState = PowerUpState(mapState)
    val tankState: TankState = TankState(gameState, soundState, mapState, powerUpState, explosionState, scoreState)
    val bulletState: BulletState = BulletState(mapState, tankState, explosionState, soundState)
    val botState: BotState = BotState(tankState, bulletState, mapState, gameState)
    val tankController: TankController = TankController(tankState, bulletState, handheldControllerState)

    init {
        tickState.addListener(mapState)
        tickState.addListener(soundState)
        tickState.addListener(scoreState)
        tickState.addListener(tankController)
        tickState.addListener(bulletState)
        tickState.addListener(botState)
        tickState.addListener( tankState)
        tickState.addListener(explosionState)
        tickState.addListener(gameState)
    }

    suspend fun startBattle(): Unit = withContext(Dispatchers.Default) {
        launch {
            tickState.start()
        }
    }

    fun resume() {
        tickState.pause(false)
    }

    fun pause() {
        tickState.pause(true)
    }
}

fun Battle.plugInDebugConfig(debugConfig: DebugConfig) {
    tickState.maxFps = debugConfig.maxFps
    botState.maxBot = debugConfig.maxBot
    bulletState.friendlyFire = debugConfig.friendlyFire
    tankState.whoIsYourDaddy = debugConfig.whoIsYourDaddy
    if (debugConfig.fixTickDelta) {
        tickState.fixTickDelta(debugConfig.tickDelta)
    } else {
        tickState.cancelFixTickDelta()
    }
}