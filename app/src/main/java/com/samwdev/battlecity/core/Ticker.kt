package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberTickState(): TickState {
    return remember { TickState() }
}

class TickState(tick: Tick = Tick.INITIAL) {
    companion object {
        private const val MAX_FPS = 1
    }

    var lastTick: Tick by mutableStateOf(tick)
        private set

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