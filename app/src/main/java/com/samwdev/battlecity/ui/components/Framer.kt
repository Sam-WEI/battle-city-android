package com.samwdev.battlecity.ui.components

import androidx.compose.runtime.*
import com.samwdev.battlecity.core.Tick
import com.samwdev.battlecity.core.TickState

val LocalTick = compositionLocalOf<Tick> {
    error("Error.")
}

@Composable
fun TickAware(tickState: TickState, content: @Composable () -> Unit) {
    val tick by tickState.tickFlow.collectAsState()
    CompositionLocalProvider(LocalTick provides tick) {
        content()
    }
}

val LocalFramer = compositionLocalOf<Int> {
    error("Error.")
}

@Composable
fun Framer(
    framesDef: List<Int>,
    infinite: Boolean = false,
    reverse: Boolean = false,
    content: @Composable () -> Unit,
) {
    val frameAcc = remember(framesDef, reverse) {
        var n = 0L
        val realFrames = if (reverse) {
            // make up a list like this: 1 2 3 4 -> 1 2 3 4 3 2 1
            // may crash if framesDef is of size 1, but that's not a use case of this class
            framesDef.toMutableList().apply {
                addAll(framesDef.reversed().subList(1, framesDef.lastIndex))
            }
        } else { framesDef }

        realFrames.map { n += it; n }
    }
    var elapsed by remember { mutableStateOf(0L) }

    val finished by remember(infinite, elapsed, frameAcc) {
        derivedStateOf {
            !infinite && elapsed >= frameAcc.last()
        }
    }

    val tick = LocalTick.current
    elapsed = remember(tick, frameAcc, finished) {
        if (!finished) {
            elapsed += tick.delta
            if (infinite) {
                elapsed %= frameAcc.last()
            } else {
                elapsed = elapsed.coerceAtMost(frameAcc.last())
            }
        }
        elapsed
    }
    val currFrame: Int by remember(elapsed, frameAcc, framesDef) {
        derivedStateOf {
            var f = 0
            while (frameAcc[f] < elapsed) { f++ }
            if (f > framesDef.lastIndex) {
                // only happens when reverse is true
                f = framesDef.lastIndex - (f + 1 - framesDef.size)
            }
            f
        }
    }

    CompositionLocalProvider(LocalFramer provides currFrame) {
        content()
    }
}