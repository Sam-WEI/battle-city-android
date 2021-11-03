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
import com.samwdev.battlecity.entity.*
import com.samwdev.battlecity.utils.logE
import com.samwdev.battlecity.utils.logI
import kotlin.math.roundToInt

@Composable
fun BattleField(
    battleState: BattleState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        battleState.start()
    }
    Map(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Black)
    ) {

        BrickLayer(mapState = battleState.mapState)
        SteelLayer(mapState = battleState.mapState)
        IceLayer(mapState = battleState.mapState)
        WaterLayer(mapState = battleState.mapState)

        Tank(tank = battleState.tankState)
        TreeLayer(mapState = battleState.mapState)

        Text(
            text = "FPS: ${(1000f / battleState.tickState.delta).roundToInt()}",
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
        val mapUnit = remember(maxWidth, MAP_BLOCK_COUNT) { maxWidth.value / MAP_BLOCK_COUNT }
        CompositionLocalProvider(LocalMapUnit provides mapUnit) {
            content()
        }
    }
}

/** Map unit */
val Int.mu: Dp @Composable get() = Dp(LocalMapUnit.current * this)
val Float.mu: Dp @Composable get() = Dp(LocalMapUnit.current * this)

private val LocalMapUnit = staticCompositionLocalOf<Float> {
    error("Not in Map composable or its child composable.")
}

@Composable
fun BrickLayer(mapState: MapState) {
    logE("recomposing bricks")
    Box(modifier = Modifier.fillMaxSize()) {
        mapState.bricks.forEach { br ->
            val (x, y) = br.offsetInMapUnit
            Box(
                modifier = Modifier
                    .offset(x.mu, y.mu)
                    .size(br.size)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
fun SteelLayer(mapState: MapState) {
    logI("recomposing steels")
    Box(modifier = Modifier.fillMaxSize()) {
        mapState.steels.forEach { el ->
            val (x, y) = el.offsetInMapUnit
            Box(
                modifier = Modifier
                    .offset(x.mu, y.mu)
                    .size(el.size)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun TreeLayer(mapState: MapState) {
    Box(modifier = Modifier.fillMaxSize()) {
        mapState.trees.forEach { el ->
            val (x, y) = el.offsetInMapUnit
            Box(
                modifier = Modifier
                    .offset(x.mu, y.mu)
                    .size(el.size)
                    .background(Color.Green)
            )
        }
    }
}

@Composable
fun WaterLayer(mapState: MapState) {
    Box(modifier = Modifier.fillMaxSize()) {
        mapState.waters.forEach { el ->
            val (x, y) = el.offsetInMapUnit
            Box(
                modifier = Modifier
                    .offset(x.mu, y.mu)
                    .size(el.size)
                    .background(Color.Blue)
            )
        }
    }
}

@Composable
fun IceLayer(mapState: MapState) {
    Box(modifier = Modifier.fillMaxSize()) {
        mapState.ices.forEach { el ->
            val (x, y) = el.offsetInMapUnit
            Box(
                modifier = Modifier
                    .offset(x.mu, y.mu)
                    .size(el.size)
                    .background(Color.White)
            )
        }
    }
}