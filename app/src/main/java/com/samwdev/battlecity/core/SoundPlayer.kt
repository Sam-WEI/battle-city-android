package com.samwdev.battlecity.core

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.samwdev.battlecity.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SoundPlayer private constructor() {
    private val soundIdMap = mutableMapOf<SoundEffect, Int>()
    private var soundPool: SoundPool? = null

    companion object {
        val INSTANCE = SoundPlayer()
    }

    suspend fun init(context: Context) {
        withContext(Dispatchers.IO) {
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
                val soundId = soundPool!!.load(context.applicationContext, it.resId, 1)
                soundIdMap[it] = soundId
            }
        }
    }

    fun play(soundEffect: SoundEffect) {
        val streamId = soundPool?.play(soundIdMap.getValue(soundEffect), 1f, 1f, 0, 0, 1f)
    }

    fun pause() {
        soundPool?.autoPause()
    }

    fun resume() {
        soundPool?.autoResume()
    }

    fun release() {
        soundPool?.release()
    }
}

enum class SoundEffect(@RawRes val resId: Int) {
    HitSteelOrBorder(R.raw.hit_steel_border),
    HitBrick(R.raw.hit_brick),
    HitArmor(R.raw.hit_armor),
    Shoot(R.raw.shoot),
    ExplosionBot(R.raw.bot_explosion),
    ExplosionPlayer(R.raw.player_explosion),
    GameOver(R.raw.game_over),
    Pause(R.raw.pause),
    SpawnPowerUp(R.raw.spawn_power_up),
    PickUpPowerUp(R.raw.pick_up_power_up),
    ScoreboardTick(R.raw.scoreboard_tick),
    DriveOnIce(R.raw.drive_on_ice),
    PickUpLifePowerUp(R.raw.pick_up_life_power_up),
}