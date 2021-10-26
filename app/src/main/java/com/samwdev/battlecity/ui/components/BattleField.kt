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
import com.samwdev.battlecity.utils.logI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BattleField(gameState: GameState, modifier: Modifier = Modifier) {
    Ticker(tickState = gameState.tickState)

    LaunchedEffect(gameState.controllerState.currentOffset) {
        // todo find another way
        snapshotFlow { gameState.controllerState.currentOffset }
            .filter { it != Offset.Unspecified }
            .collect {
                logI("moving tank")
                val (xo, yo) = it
                gameState.tankState.x += (5 * xo).roundToInt()
                gameState.tankState.y += (5 * yo).roundToInt()
            }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Green)) {
        Text(text = "tick: ${gameState.tickState.uptimeMillis}. delta: ${gameState.tickState.delta}.")
        Tank(tank = gameState.tankState, tickState = gameState.tickState)
    }

}