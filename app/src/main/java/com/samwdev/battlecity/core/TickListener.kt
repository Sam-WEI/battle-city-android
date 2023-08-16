package com.samwdev.battlecity.core

import com.samwdev.battlecity.core.state.Tick

abstract class TickListener {
    private var active = true

    abstract fun onTick(tick: Tick)

    fun deactivate() {
        active = false
    }

    fun activate() {
        active = true
    }

    fun onTickInternal(tick: Tick) {
        if (active) {
            onTick(tick)
        }
    }
}