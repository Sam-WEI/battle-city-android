package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.TankState
import com.samwdev.battlecity.core.ticker

@Composable
fun Tank(tank: TankState) {
    val tick by ticker()
    Text(text = "tick: ${tick.uptimeMillis}. delta: ${tick.delta}", modifier = Modifier.offset(y = 40.dp))
    Canvas(modifier = Modifier.size(50.dp, 50.dp)) {
        drawRect(Color.Black, topLeft = Offset(tank.x.toFloat(), tank.y.toFloat()), size = size)
    }
}