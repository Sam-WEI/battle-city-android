package com.samwdev.battlecity.core

import androidx.compose.runtime.*

@Composable
fun rememberSoundState() : SoundState {
    return remember { SoundState() }
}

class SoundState : TickListener {
    private val soundPlayer: SoundPlayer = SoundPlayer.INSTANCE

    private var soundToPlay by mutableStateOf<Set<SoundEffect>>(setOf())

    fun playSound(soundEffect: SoundEffect) {
        soundToPlay = soundToPlay + soundEffect
    }

    override fun onTick(tick: Tick) {
        for (sound in soundToPlay) {
            soundPlayer.play(sound)
        }
        soundToPlay = setOf()
    }
}