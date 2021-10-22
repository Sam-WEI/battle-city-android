package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.TankModel
import kotlinx.coroutines.launch

@Composable
fun BattleField(modifier: Modifier = Modifier) {
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
        Tank(tank = TankModel(x = 200, y = 200, direction = Direction.Down))
    }
}