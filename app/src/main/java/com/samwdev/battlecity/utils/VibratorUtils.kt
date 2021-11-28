package com.samwdev.battlecity.utils

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibratorHelper(context: Context) {
    private val vibrator: Vibrator = context.applicationContext
        .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build()

    fun vibrateJoyStick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(10, 40),
                audioAttributes
            )
        } else  {
            vibrator.vibrate(1, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
        }
    }

    fun vibrateFire() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK),
                audioAttributes
            )
        } else  {
            vibrator.vibrate(10, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
        }
    }

    fun vibrateSmallExplosion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE),
                audioAttributes
            )
        } else  {
            vibrator.vibrate(10, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
        }
    }

    fun vibrateLargeExplosion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(10, 255),
                audioAttributes
            )
        } else  {
            vibrator.vibrate(10, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
        }
    }
}