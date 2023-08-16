package com.samwdev.battlecity.core.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.samwdev.battlecity.core.SoundEffect
import com.samwdev.battlecity.core.SoundPlayer
import com.samwdev.battlecity.core.TickListener

class SoundState : TickListener() {
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