package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.TankState
import com.samwdev.battlecity.core.TickState

@Composable
fun Tank(tank: TankState, tickState: TickState) {
    Canvas(modifier = Modifier.size(50.dp, 50.dp)) {
        drawRect(
            Color.Black,
            topLeft = Offset(tank.x.toFloat(), tank.y.toFloat()),
            size = size,
            alpha = (tickState.uptimeMillis % 1500) / 3000f + 0.5f
        )
    }
}