package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Composable
fun rememberTickState(): TickState {
    return remember { TickState() }
}

class TickState(tick: Tick = Tick.INITIAL) {
    companion object {
        const val MAX_FPS = 120
    }

    private var paused = false

    private var lastTick: Tick = tick
    var fps: Int by mutableStateOf(0)
        private set

    private var lastUptime = lastTick.uptimeMillis
    private var tickCount = 0

    val uptimeMillis: Long
        get() = lastTick.uptimeMillis
    val delta: Int
        get() = lastTick.delta

    private var fixedDelta: Int? = null
    var maxFps: Int = MAX_FPS

    private val _tickFlow: MutableStateFlow<Tick> = MutableStateFlow(Tick.INITIAL)
    val tickFlow: StateFlow<Tick> = _tickFlow

    private suspend fun update(now: Long) {
        val delta = now - lastTick.uptimeMillis
        if (delta > 1000f / maxFps) {
            val newTick = Tick(now, fixedDelta ?: delta.toInt())
            if (lastTick != Tick.INITIAL) {
                if (!paused) {
                    _tickFlow.emit(newTick)
                    tickCount++
                }
            }
            lastTick = newTick
        }
        (now - lastUptime).takeIf { it > 1000f }?.let { elapsed ->
            fps = (tickCount.toFloat() / elapsed * 1000).roundToInt()
            lastUptime = now
            tickCount = 0
        }
    }

    suspend fun start(): Unit = coroutineScope {
        while (true) {
            val now = withFrameMillis { it }
            update(now)
        }
    }

    fun pause(b: Boolean) {
        paused = b
    }

    fun fixTickDelta(delta: Int) {
        fixedDelta = delta
    }

    fun cancelFixTickDelta() {
        fixedDelta = null
    }
}

data class Tick(val uptimeMillis: Long, val delta: Int) {
    companion object {
        val INITIAL = Tick(0, 0)
    }
}