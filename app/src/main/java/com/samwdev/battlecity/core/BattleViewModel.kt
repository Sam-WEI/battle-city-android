package com.samwdev.battlecity.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samwdev.battlecity.entity.StageConfig
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BattleViewModel(
    stageConfig: StageConfig,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val tickState = TickState()
    val soundState = SoundState(viewModelScope)
    val explosionState = ExplosionState()
    val handheldControllerState = HandheldControllerState()

    val mapState: MapState = MapState(stageConfig)
    val powerUpState: PowerUpState = PowerUpState(mapState)
    val tankState: TankState = TankState(soundState, mapState, powerUpState, explosionState)
    val bulletState: BulletState = BulletState(mapState, tankState, explosionState, soundState)
    val botState: BotState = BotState(tankState, bulletState, mapState)
    val tankController: TankController = TankController(tankState, bulletState, handheldControllerState)

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch {
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

    suspend fun start() {
        tickState.start()
    }

    fun resume() {
        tickState.pause(false)
    }

    fun pause() {
        tickState.pause(true)
    }
}