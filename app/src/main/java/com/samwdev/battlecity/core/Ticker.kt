package com.samwdev.battlecity.core

import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.coroutines.CoroutineContext

class Ticker : CoroutineScope, LifecycleObserver {
    companion object {
        private const val MAX_FPS = 5
        private const val FRAME_DUR = (1000f / MAX_FPS).toLong()
    }
    private val _mutableStateFlow = MutableSharedFlow<Long>(0, onBufferOverflow = BufferOverflow.SUSPEND)
    val flow = _mutableStateFlow.asSharedFlow()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    fun start() {
        launch {
            var lastEmit = SystemClock.uptimeMillis()
            while (true) {
                val now = SystemClock.uptimeMillis()
                val delta = now - lastEmit
                if (delta > FRAME_DUR) {
                    _mutableStateFlow.emit(delta)
                    lastEmit = now
                }
                delay(FRAME_DUR)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
        coroutineContext[Job]?.cancel()
    }
}