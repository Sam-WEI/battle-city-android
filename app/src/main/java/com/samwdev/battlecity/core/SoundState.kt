package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSoundState(coroutine: CoroutineScope) : SoundState {
    return remember { SoundState(coroutine) }
}

class SoundState(
    private val coroutine: CoroutineScope
) : TickListener {
    private val soundPlayer: SoundPlayer = SoundPlayer.INSTANCE

    private var soundToPlay by mutableStateOf<Set<SoundEffect>>(setOf())

    fun playSound(soundEffect: SoundEffect) {
        soundToPlay = soundToPlay + soundEffect
    }

    override fun onTick(tick: Tick) {
        coroutine.launch {
            for (sound in soundToPlay) {
                soundPlayer.play(sound)
            }
        }
        soundToPlay = setOf()
    }
}