package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.*

@Composable
fun BattleField(
    battleState: BattleState,
    modifier: Modifier = Modifier,
) {
    TickAware(tickState = battleState.tickState) {
        Map(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black)
        ) {
            BrickLayer(battleState.mapState.bricks)
            SteelLayer(battleState.mapState.steels)
            IceLayer(battleState.mapState.ices)
            Framer(
                framesDef = listOf(700, 700),
                infinite = true,
            ) {
                WaterLayer(battleState.mapState.waters)
            }
            EagleLayer(battleState.mapState.eagle)

            battleState.tankState.tanks.forEach { (id, tank) ->
                Tank(tank = tank)
            }

            battleState.bulletState.bullets.forEach { (id, bullet) ->
                Bullet(bullet)
            }

            TreeLayer(battleState.mapState.trees)

            SpawnBlink(topLeft = Offset(0f, 0f))

            Text(
                text = "FPS ${battleState.tickState.fps}",
                color = Color.Green,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun Map(
    modifier: Modifier,
    sideBlockCount: Int = MAP_BLOCK_COUNT,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val mapPixelInDp = remember(maxWidth) { maxWidth / (sideBlockCount.grid2mpx) }
        CompositionLocalProvider(LocalMapPixelDp provides mapPixelInDp) {
            content()
        }
    }
}

/** MapPixel to Dp */
val MapPixel.mpx2dp: Dp @Composable get() = LocalMapPixelDp.current * this

/**
 * Provides dp size for one MapPixel
 */
val LocalMapPixelDp = staticCompositionLocalOf<Dp> {
    error("Not in Map composable or its child composable.")
}

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
    val frameAcc = remember(framesDef) {
        var n = 0L
        val realFrames = if (reverse) {
            framesDef.toMutableList().apply {
                addAll(framesDef.reversed().subList(1, framesDef.lastIndex))
            }
        } else {
            framesDef
        }
        realFrames.map {
            n += it
            n
        }
    }
    var elapsed by remember { mutableStateOf(0L) }
    val finished = !infinite && elapsed >= frameAcc.last()
    var currFrame = 0

    if (!finished) {
        val tick = LocalTick.current
        elapsed += tick.delta
        if (infinite) {
            elapsed %= frameAcc.last()
        } else {
            elapsed = elapsed.coerceAtMost(frameAcc.last())
        }
    }
    while (frameAcc[currFrame] < elapsed) {
        currFrame++
    }
    if (currFrame > framesDef.lastIndex) {
        // only happens when reverse is true
        currFrame = framesDef.lastIndex - (currFrame + 1 - framesDef.size)
    }
    CompositionLocalProvider(LocalFramer provides currFrame) {
        content()
    }
}