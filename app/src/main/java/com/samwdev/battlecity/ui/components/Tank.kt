package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.TankState

@Composable
fun Tank(tank: TankState) {
    Canvas(modifier = Modifier.size(50.dp, 50.dp)) {
        drawRect(Color.Black, topLeft = Offset(tank.x.toFloat(), tank.y.toFloat()), size = size)
    }
}