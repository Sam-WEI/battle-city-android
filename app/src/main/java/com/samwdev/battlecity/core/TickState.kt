package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun rememberTickState(): TickState {
    return remember { TickState() }
}

class TickState(tick: Tick = Tick.INITIAL) {
    companion object {
        private const val MAX_FPS = 100
    }

    var lastTick: Tick by mutableStateOf(tick)
        private set

    var fps: Int by mutableStateOf(0)
        private set

    private var lastUptime by mutableStateOf(lastTick.uptimeMillis)
    private var tickCount by mutableStateOf(0)

    val uptimeMillis: Long
        get() = lastTick.uptimeMillis
    val delta: Long
        get() = lastTick.delta

    private val _tickFlow: MutableStateFlow<Tick> = MutableStateFlow(Tick.INITIAL)
    val tickFlow: StateFlow<Tick> = _tickFlow

    suspend fun update(now: Long) {
        val delta = now - lastTick.uptimeMillis
        if (delta > 1000f / MAX_FPS) {
            lastTick = Tick(now, delta)
            _tickFlow.emit(lastTick)
            tickCount++
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
}

data class Tick(val uptimeMillis: Long, val delta: Long) {
    companion object {
        val INITIAL = Tick(0, 0)
    }
}