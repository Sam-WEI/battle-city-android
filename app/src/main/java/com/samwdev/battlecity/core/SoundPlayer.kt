package com.samwdev.battlecity.core

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.samwdev.battlecity.R

class SoundPlayer private constructor() {
    private val soundIdMap = mutableMapOf<SoundEffect, Int>()
    private lateinit var soundPool: SoundPool

    companion object {
        val INSTANCE = SoundPlayer()
    }

    suspend fun init(context: Context) {
        soundPool = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
            )
            .setMaxStreams(5)
            .build()

        SoundEffect.values().forEach {
            val soundId = soundPool.load(context.applicationContext, it.resId, 1)
            soundIdMap[it] = soundId
        }
    }

    suspend fun play(soundEffect: SoundEffect) {
        val streamId = soundPool.play(soundIdMap.getValue(soundEffect), 1f, 1f, 0, 0, 1f)
    }

    suspend fun pause() {
        soundPool.autoPause()
    }

    suspend fun resume() {
        soundPool.autoResume()
    }

    suspend fun release() {
        soundPool.release()
    }
}

enum class SoundEffect(@RawRes val resId: Int) {
    BulletHitSteel(R.raw.bullet_hit_steel),
    BulletHitBrick(R.raw.bullet_hit_brick),
    BulletShot(R.raw.bullet_shot),
    Explosion1(R.raw.explosion_1),
    Explosion2(R.raw.explosion_2),
    GameOver(R.raw.game_over),
    Pause(R.raw.pause),
    PowerUpAppear(R.raw.powerup_appear),
    PowerUpPick(R.raw.powerup_pick),
}