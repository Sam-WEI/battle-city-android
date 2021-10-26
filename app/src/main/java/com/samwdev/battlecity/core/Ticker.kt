package com.samwdev.battlecity.core

import android.os.SystemClock
import androidx.compose.runtime.*
import com.samwdev.battlecity.utils.logD
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class Ticker : BaseHandler() {
    companion object {
        private const val MAX_FPS = 2
        private const val FRAME_DUR = (1000f / MAX_FPS).toLong()
    }
    private val _mutableStateFlow = MutableSharedFlow<Long>(0, onBufferOverflow = BufferOverflow.SUSPEND)
    val flow = _mutableStateFlow.asSharedFlow()

    fun start() {
        launch(coroutineContext) {
            var lastEmit = SystemClock.uptimeMillis()
            var i = 0
            while (isActive) {
                val now = SystemClock.uptimeMillis()
                val delta = now - lastEmit
                if (delta > FRAME_DUR) {
                    _mutableStateFlow.emit(delta)
                    lastEmit = now
                }
                logD("ticking (${i++}): delta - ${delta}.")
                delay(FRAME_DUR)
            }
        }
    }
}

@Composable
fun Ticker(
    tickState: TickState = rememberTickState()
) {
    produceState(initialValue = tickState) {
        while (true) {
            val now = withFrameMillis { it }
            tickState.update(now)
        }
    }
}

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

    val uptimeMillis: Long = lastTick.uptimeMillis
    val delta: Long = lastTick.delta

    private val _tickFlow: MutableStateFlow<Tick> = MutableStateFlow(Tick.INITIAL)
    val tickFlow: StateFlow<Tick> = _tickFlow

    suspend fun update(now: Long) {
        if (now - uptimeMillis > 1000f / MAX_FPS) {
            val delta = now - uptimeMillis
            lastTick = Tick(uptimeMillis, delta)
            _tickFlow.emit(lastTick)
        }
    }
}

data class Tick(val uptimeMillis: Long, val delta: Long) {
    companion object {
        val INITIAL = Tick(0, 0)
    }
}