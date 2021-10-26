package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.utils.logE
import kotlinx.coroutines.launch

@Composable
fun BattleField(gameState: GameState, modifier: Modifier = Modifier) {
    val ticker by ticker()

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
        Tank(tank = gameState.tank, count = count)
    }

}