package com.samwdev.battlecity.core

import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleObserver
import com.samwdev.battlecity.utils.logD
import com.samwdev.battlecity.utils.logE
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.coroutineContext
import kotlin.coroutines.CoroutineContext

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

class TickState {
    companion object {
        private const val MAX_FPS = 10
    }

    var uptimeMillis: Long by mutableStateOf(SystemClock.uptimeMillis())
        private set
    var delta: Long by mutableStateOf(0)
        private set

    fun update(now: Long) {
        if (now - uptimeMillis > 1000f / MAX_FPS) {
            delta = now - uptimeMillis
            uptimeMillis = now
        }
    }
}
