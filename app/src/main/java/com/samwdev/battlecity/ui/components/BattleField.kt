package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.utils.logI

@Composable
fun BattleField(
    battleState: BattleState,
    modifier: Modifier = Modifier,
) {
    Map(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Black)
    ) {
        BrickLayer(battleState.mapState.bricks)
        SteelLayer(battleState.mapState.steels)
        IceLayer(battleState.mapState.ices)
        Framer(
            tickState = battleState.tickState,
            framesDef = listOf(700, 700),
            infinite = true,
        ) {
            WaterLayer(battleState.mapState.waters)
        }
        EagleLayer(battleState.mapState.eagle)

        TickAware(tickState = battleState.tickState) {
            battleState.tankState.tanks.forEach { (id, tank) ->
                Tank(tank = tank)
            }
        }

        battleState.bulletState.bullets.forEach { (id, bullet) ->
            Bullet(bullet)
        }

        TreeLayer(battleState.mapState.trees)

        Text(
            text = "FPS ${battleState.tickState.fps}",
            color = Color.Green,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )
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
    tickState: TickState,
    framesDef: List<Int>,
    infinite: Boolean = false,
    content: @Composable () -> Unit,
) {
    val frameAcc = remember(framesDef) {
        var n = 0L
        framesDef.map {
            n += it
            n
        }
    }
    var elapsed by remember { mutableStateOf(0L) }
    val finished = !infinite && elapsed >= frameAcc.last()
    var currFrame = 0

    if (!finished) {
        val tick by tickState.tickFlow.collectAsState()
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
    logI("curr: ${currFrame}")
    CompositionLocalProvider(LocalFramer provides currFrame) {
        content()
    }
}