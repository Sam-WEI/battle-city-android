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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.BattleState
import com.samwdev.battlecity.core.Bullet
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MAP_PIXEL_IN_EACH_BLOCK

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
private fun Map(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val mapPixelInDp = remember(maxWidth) { maxWidth / (MAP_BLOCK_COUNT * MAP_PIXEL_IN_EACH_BLOCK) }
        CompositionLocalProvider(LocalMapPixelDp provides mapPixelInDp) {
            content()
        }
    }
}

/** Map pixel in Dp */
val Int.mpDp: Dp @Composable get() = LocalMapPixelDp.current * this
val Float.mpDp: Dp @Composable get() = LocalMapPixelDp.current * this

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
                size = Size(el.elementSizeInMapPixel * mpx.toPx(), el.elementSizeInMapPixel * mpx.toPx()),
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
                size = Size(el.elementSizeInMapPixel * mpx.toPx(), el.elementSizeInMapPixel * mpx.toPx()),
            )
        }
    }
}

@Composable
fun TreeLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.trees.forEach { el ->
            drawRect(
                color = Color(78, 134, 22, 151),
                topLeft = el.offsetInMapPixel * mpx.toPx(),
                size = Size(el.elementSizeInMapPixel * mpx.toPx(), el.elementSizeInMapPixel * mpx.toPx()),
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
                size = Size(el.elementSizeInMapPixel * mpx.toPx(), el.elementSizeInMapPixel * mpx.toPx()),
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
                size = Size(el.elementSizeInMapPixel * mpx.toPx(), el.elementSizeInMapPixel * mpx.toPx()),
            )
        }
    }
}

@Composable
fun EagleLayer(mapState: MapState) {
    val mpx = LocalMapPixelDp.current
    val size = mapState.eagle.elementSizeInMapPixel.mpDp
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawOval(
            color = Color.Red,
            topLeft = mapState.eagle.offsetInMapPixel * mpx.toPx(),
            size = Size(size.toPx(), size.toPx())
        )
    }
}