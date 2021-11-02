package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.*
import kotlin.math.roundToInt

@Composable
fun BattleField(battleState: BattleState, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        battleState.start()
    }
    Box(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Black)) {
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
fun MapBackground() {
    Box() {

    }
}