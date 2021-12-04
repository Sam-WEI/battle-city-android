package com.samwdev.battlecity.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.OnScreenScore

@Composable
fun OnScreenScore(onScreenScore: OnScreenScore) {
    PixelCanvas(topLeftInMapPixel = onScreenScore.offset) {
        drawText(
            text = onScreenScore.score.toString(),
            color = Color.White,
            offset = Offset(0f, 12f),
            fontSize = 2.sp
        )
    }
}