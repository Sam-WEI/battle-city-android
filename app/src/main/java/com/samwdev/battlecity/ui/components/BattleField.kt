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
fun BattleField(gameState: GameState, tanksViewModel: TanksViewModel, modifier: Modifier = Modifier) {
    val tanks by tanksViewModel.tanks.collectAsState()
    val tank by tanksViewModel.tank.collectAsState()
    val ticker by ticker()
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
        Text(text = "tick: ${ticker.uptimeMillis}. delta: ${ticker.delta}")
        Tank(tank = tank)
    }

}