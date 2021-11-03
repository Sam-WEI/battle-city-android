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
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.MAP_BLOCK_COUNT
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.TreeElement
import kotlin.math.roundToInt

@Composable
fun BattleField(
    battleState: BattleState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        battleState.start()
    }
    val brickElements = remember(battleState.mapState.bricks) {
        battleState.mapState.bricks.map { BrickElement(it) }
    }
    val steelElements = remember(battleState.mapState.steels) {
        battleState.mapState.steels.map { SteelElement(it) }
    }
    val treeElements = remember(battleState.mapState.trees) {
        battleState.mapState.trees.map { TreeElement(it) }
    }
    Map(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Black)
    ) {
        TreeLayer(trees = treeElements)
        BrickLayer(bricks = brickElements)
        SteelLayer(steel = steelElements)
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

private val LocalMapUnit = staticCompositionLocalOf<Float> {
    error("Not in Map composable or its child composable.")
}

@Composable
fun BrickLayer(bricks: List<BrickElement>) {
    Box(modifier = Modifier.fillMaxSize()) {
        bricks.take(15000).forEach { br ->
            val (x, y) = br.offsetInMapUnit
            Box(
                modifier = Modifier.offset(x.mu, y.mu)
                    .size(br.size)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
fun SteelLayer(steel: List<SteelElement>) {
    Box(modifier = Modifier.fillMaxSize()) {
        steel.forEach { el ->
            val (x, y) = el.offsetInMapUnit
            Box(
                modifier = Modifier.offset(x.mu, y.mu)
                    .size(el.size)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun TreeLayer(trees: List<TreeElement>) {
    Box(modifier = Modifier.fillMaxSize()) {
        trees.forEach { el ->
            val (x, y) = el.offsetInMapUnit
            Box(
                modifier = Modifier.offset(x.mu, y.mu)
                    .size(el.size)
                    .background(Color.Green)
            )
        }
    }
}