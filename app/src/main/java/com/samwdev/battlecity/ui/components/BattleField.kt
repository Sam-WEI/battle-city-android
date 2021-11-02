package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.entity.MAP_BLOCK_COUNT
import kotlin.math.min
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
        .background(Color.Black)) {
        TreeLayer(trees = battleState.mapState.trees)
        BrickLayer(bricks = battleState.mapState.bricks)
        Text(
            text = "FPS: ${(1000f / battleState.tickState.delta).roundToInt()}",
            color = Color.Green,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        Tank(tank = battleState.tankState)

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
        val mapUnit = maxWidth.value / MAP_BLOCK_COUNT
        CompositionLocalProvider(LocalMapUnit provides mapUnit) {
            content()
        }
    }
}

/** Map unit */
val Int.mu: Dp @Composable get() = Dp(LocalMapUnit.current * this)
val Float.mu: Dp @Composable get() = Dp(LocalMapUnit.current * this)

@Composable
fun ProvideGameUiUnit(
    content: @Composable () -> Unit,
) {
    val mapSide = with (LocalContext.current.resources.displayMetrics) {
        min(widthPixels, heightPixels)
    }
    CompositionLocalProvider(LocalMapUnit provides 5f, content = content)
}

val LocalMapUnit = staticCompositionLocalOf<Float> {
    error("Not in Map composable or its child composable.")
}

@Composable
fun BrickLayer(bricks: List<Int>) {
    Box {
        Text(text = bricks.joinToString(), color = Color.White)
    }
}

@Composable
fun TreeLayer(trees: List<Int>) {
    Box {
        Text("TREES", color = Color.White)
        Text(text = trees.joinToString(), color = Color.White)
    }
}