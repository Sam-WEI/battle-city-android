package com.samwdev.battlecity.core.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.platform.AndroidUiDispatcher
import com.samwdev.battlecity.core.TickListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class TickState(tick: Tick = Tick.INITIAL) {
    companion object {
        const val MAX_FPS = 120
    }

    private val tickListeners: MutableList<TickListener> = mutableListOf()

    private var paused = false

    private var lastTick: Tick = tick
    var fps: Int by mutableIntStateOf(0)
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
    val tickFlow: StateFlow<Tick> = _tickFlow.asStateFlow()

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

    fun addListener(tickListener: TickListener) {
        tickListeners.add(tickListener)
    }

    suspend fun start() = withContext(AndroidUiDispatcher.Main) {
        launch {
            while (isActive) {
                val now = withFrameMillis { it }
                update(now)
            }
        }

        launch {
            tickFlow.collect { tick ->
                tickListeners.forEach { it.onTickInternal(tick) }
            }
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