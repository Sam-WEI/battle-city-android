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

@Composable
fun BattleField(gameState: BattleState, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        gameState.start()
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Green)) {
        Text(text = "tick: ${gameState.tickState.uptimeMillis}. delta: ${gameState.tickState.delta}.")
        Tank(tank = gameState.tankState)
    }

}