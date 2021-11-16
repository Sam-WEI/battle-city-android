package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.entity.TreeElement

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
        BrickLayer(mapState = battleState.mapState)
        SteelLayer(mapState = battleState.mapState)
        IceLayer(mapState = battleState.mapState)
        WaterLayer(mapState = battleState.mapState)
        EagleLayer(mapState = battleState.mapState)

        battleState.tankState.tanks.forEach { (id, tank) ->
            Tank(tank = tank)
        }

        battleState.bulletState.bullets.forEach { (id, bullet) ->
            Bullet(bullet)
        }

        TreeLayer(mapState = battleState.mapState)

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
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val mapPixelInDp = remember(maxWidth) { maxWidth / (MAP_BLOCK_COUNT.grid2mpx) }
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

@Composable
fun BrickLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.bricks.forEach { el ->
            drawRect(
                color = Color(97, 20 ,9),
                topLeft = el.offsetInMapPixel * mpx.toPx(),
                size = Size(el.elementSize * mpx.toPx(), el.elementSize * mpx.toPx()),
            )
        }
    }
}

@Composable
fun SteelLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.steels.forEach { el ->
            drawRect(
                color = Color.Gray,
                topLeft = el.offsetInMapPixel * mpx.toPx(),
                size = Size(el.elementSize * mpx.toPx(), el.elementSize * mpx.toPx()),
            )
        }
    }
}

@Composable
fun WaterLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.waters.forEach { el ->
            drawRect(
                color = Color.Blue,
                topLeft = el.offsetInMapPixel * mpx.toPx(),
                size = Size(el.elementSize * mpx.toPx(), el.elementSize * mpx.toPx()),
            )
        }
    }
}

@Composable
fun IceLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.ices.forEach { el ->
            drawRect(
                color = Color.White,
                topLeft = el.offsetInMapPixel * mpx.toPx(),
                size = Size(el.elementSize * mpx.toPx(), el.elementSize * mpx.toPx()),
            )
        }
    }
}

@Composable
fun EagleLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    val size = mapState.eagle.elementSize.mpx2dp
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawOval(
            color = Color.Red,
            topLeft = mapState.eagle.offsetInMapPixel * mpx.toPx(),
            size = Size(size.toPx(), size.toPx())
        )
    }
}