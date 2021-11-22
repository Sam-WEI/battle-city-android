package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.PowerUp
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val PowerUpColorBlue = Color(43, 71, 121)

@Composable
fun PowerUp(topLeft: Offset, powerUp: PowerUp) {
    PixelCanvas(
        topLeftInMapPixel = topLeft,
        widthInMapPixel = 1f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
    ) {
        drawHorizontalLine(color = Color.White, topLeft = Offset(1f, 0f), length = 13f)
        drawVerticalLine(color = Color.White, topLeft = Offset(0f, 1f), length = 12f)
        drawVerticalLine(color = Color.White, topLeft = Offset(14f, 1f), length = 12f)
        drawHorizontalLine(color = Color.White, topLeft = Offset(1f, 13f), length = 13f)

        drawHorizontalLine(color = PowerUpColorBlue, topLeft = Offset(1f, 14f), length = 13f)
        drawVerticalLine(color = PowerUpColorBlue, topLeft = Offset(15f, 1f), length = 13f)

        drawRect(color = PowerUpColorBlue, topLeft = Offset(2f, 2f), size = Size(12f, 11f))
    }
}

@Preview
@Composable
private fun PowerUpPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(200.dp), sideBlockCount = 5) {
            PowerUp(topLeft = Offset(0f, 0f), PowerUp.Helmet)
        }
    }
}