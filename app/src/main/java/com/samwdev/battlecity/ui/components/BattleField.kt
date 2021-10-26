package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BattleField(gameState: GameState, modifier: Modifier = Modifier) {
    val ticker by ticker()

    LaunchedEffect(gameState.controllerState.currentOffset) {
        snapshotFlow { gameState.controllerState.currentOffset }
            .filter { it != Offset.Unspecified }
            .collect {
                val (xo, yo) = it
                gameState.tankState.x += (5 * xo).roundToInt()
                gameState.tankState.y += (5 * yo).roundToInt()
            }
    }

    val count by remember(ticker) {
        derivedStateOf { ticker.uptimeMillis % 2 }
    }
    LaunchedEffect(Unit) {
        launch {
            var last = withFrameMillis { it }
            while (true) {
                withFrameMillis {
                    last = it
                }
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Green)) {
        Text(text = "tick: ${ticker.uptimeMillis}. delta: ${ticker.delta}. count: ${count}")
        Tank(tank = gameState.tankState, count = count)
    }

}