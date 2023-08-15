package com.samwdev.battlecity.utils

import android.util.Log
import com.samwdev.battlecity.BuildConfig

object Logger {
    fun debug(msg: String) {
        Log.d(null, msg)
    }

    fun info(msg: String) {
        Log.i(null, msg)
    }

    fun warn(msg: String) {
        Log.w(null, msg)
    }

    fun error(msg: String) {
        Log.e(null, msg)
    }

    private fun systemLog(level: Int, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.println(level, "battle_city", msg)
        }
    }
}