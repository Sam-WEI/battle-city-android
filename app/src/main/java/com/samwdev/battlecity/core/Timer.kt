package com.samwdev.battlecity.core

class Timer(timeInMs: Int = 0) {
    private var timeConfig = timeInMs

    var remainingTime = timeConfig
        private set

    val timeUp: Boolean get() = remainingTime < 0

    var isActive: Boolean = false
        private set

    /**
     * Returns true if this very tick times up the timer.
     */
    fun tick(tick: Tick): Boolean {
        if (!isActive) {
            throw IllegalStateException("Timer is not active")
        }
        val beforeTick = remainingTime
        remainingTime -= tick.delta
        if (remainingTime < 0) {
            isActive = false
        }
        return beforeTick >= 0 && remainingTime < 0
    }

    fun reset(time: Int = timeConfig) {
        remainingTime = time
    }

    fun activate() {
        isActive = true
    }

    fun resetAndActivate(time: Int = timeConfig) {
        reset(time)
        activate()
    }
}