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
fun ticker(): State<Tick> {
    var lastTick by remember { mutableStateOf(SystemClock.uptimeMillis()) }
    return produceState(initialValue = Tick(SystemClock.uptimeMillis(), 0)) {
        while (true) {
            val now = withFrameMillis { it }
            value = Tick(now, now - lastTick)
            lastTick = now
        }
    }
}

data class Tick(val uptimeMillis: Long, val delta: Long)