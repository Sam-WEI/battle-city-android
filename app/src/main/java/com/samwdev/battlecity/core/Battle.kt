package com.samwdev.battlecity.core

import com.samwdev.battlecity.entity.StageConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Battle(
    private val gameState: GameState,
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

    suspend fun startBattle(): Unit = withContext(Dispatchers.Default) {
        launch {
            tickState.tickFlow.collect { tick ->
                mapState.onTick(tick)
                soundState.onTick(tick)
                scoreState.onTick(tick)
                tankController.onTick(tick)
                bulletState.onTick(tick)
                botState.onTick(tick)
                tankState.onTick(tick)
                explosionState.onTick(tick)
                gameState.onTick(tick)
            }
        }
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
