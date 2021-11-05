package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import com.samwdev.battlecity.core.BattleState
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.entity.MAP_BLOCK_COUNT

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
        val mapUnit = remember(maxWidth, MAP_BLOCK_COUNT) { maxWidth / MAP_BLOCK_COUNT }
        CompositionLocalProvider(LocalMapUnitDp provides mapUnit) {
            content()
        }
    }
}

/** Map unit */
val Int.mu: Dp @Composable get() = LocalMapUnitDp.current * this
val Float.mu: Dp @Composable get() = LocalMapUnitDp.current * this

private val LocalMapUnitDp = staticCompositionLocalOf<Dp> {
    error("Not in Map composable or its child composable.")
}

@Composable
fun BrickLayer(mapState: MapState) {
    val mu = LocalMapUnitDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.bricks.forEach { el ->
            drawRect(
                color = Color(97, 20 ,9),
                topLeft = el.offsetInMapUnit * mu.toPx(),
                size = Size(el.sizeInMapUnit * mu.toPx(), el.sizeInMapUnit * mu.toPx()),
            )
        }
    }
}

@Composable
fun SteelLayer(mapState: MapState) {
    val mu = LocalMapUnitDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.steels.forEach { el ->
            drawRect(
                color = Color.Gray,
                topLeft = el.offsetInMapUnit * mu.toPx(),
                size = Size(el.sizeInMapUnit * mu.toPx(), el.sizeInMapUnit * mu.toPx()),
            )
        }
    }
}

@Composable
fun TreeLayer(mapState: MapState) {
    val mu = LocalMapUnitDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.trees.forEach { el ->
            drawRect(
                color = Color(78, 134, 22, 151),
                topLeft = el.offsetInMapUnit * mu.toPx(),
                size = Size(el.sizeInMapUnit * mu.toPx(), el.sizeInMapUnit * mu.toPx()),
            )
        }
    }
}

@Composable
fun WaterLayer(mapState: MapState) {
    val mu = LocalMapUnitDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.waters.forEach { el ->
            drawRect(
                color = Color.Blue,
                topLeft = el.offsetInMapUnit * mu.toPx(),
                size = Size(el.sizeInMapUnit * mu.toPx(), el.sizeInMapUnit * mu.toPx()),
            )
        }
    }
}

@Composable
fun IceLayer(mapState: MapState) {
    val mu = LocalMapUnitDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        mapState.ices.forEach { el ->
            drawRect(
                color = Color.White,
                topLeft = el.offsetInMapUnit * mu.toPx(),
                size = Size(el.sizeInMapUnit * mu.toPx(), el.sizeInMapUnit * mu.toPx()),
            )
        }
    }
}

@Composable
fun EagleLayer(mapState: MapState) {
    val mu = LocalMapUnitDp.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawOval(
            color = Color.Red,
            topLeft = mapState.eagle.toOffset() * mu.toPx(),
            size = Size(mu.toPx(), mu.toPx())
        )
    }
}