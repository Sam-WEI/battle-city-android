package com.samwdev.battlecity.core

import com.samwdev.battlecity.utils.logD
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BulletHandler(
    private val ticker: Ticker
) : BaseHandler() {

    fun start() {
        launch {
            ticker.flow.collect {
                logD("bullets processing... $it")
            }
        }
    }
}