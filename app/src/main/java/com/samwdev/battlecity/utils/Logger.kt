package com.samwdev.battlecity.utils

import android.util.Log
import com.samwdev.battlecity.BuildConfig

object Logger {
    fun debug(msg: String) {
        systemLog(Log.DEBUG, msg)
    }

    fun info(msg: String) {
        systemLog(Log.INFO, msg)
    }

    fun warn(msg: String) {
        systemLog(Log.WARN, msg)
    }

    fun error(msg: String) {
        systemLog(Log.ERROR, msg)
    }

    private fun systemLog(level: Int, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.println(level, "battle_city", msg)
        }
    }
}