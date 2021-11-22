package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.PowerUp
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val PowerUpColorBlue = Color(43, 71, 121)
private val PowerUpColorGray = Color(181, 181, 181)

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
        drawPixel(color = PowerUpColorGray, topLeft = Offset(14f, 0f))
        drawPixel(color = PowerUpColorGray, topLeft = Offset(0f, 13f))
        drawPixel(color = PowerUpColorGray, topLeft = Offset(14f, 13f))

        drawHorizontalLine(color = PowerUpColorBlue, topLeft = Offset(1f, 14f), length = 14f)
        drawVerticalLine(color = PowerUpColorBlue, topLeft = Offset(15f, 1f), length = 13f)

        drawRect(color = PowerUpColorBlue, topLeft = Offset(2f, 2f), size = Size(12f, 11f))
        translate(2f, 2f) {
            this as PixelDrawScope
            when (powerUp) {
                PowerUp.Helmet -> drawHelmet()
                PowerUp.Star -> drawStar()
                PowerUp.Grenade -> drawGrenade()
                PowerUp.Tank -> drawTank()
                PowerUp.Shovel -> drawShovel()
                PowerUp.Timer -> drawTimer()
            }
        }
    }
}

private fun PixelDrawScope.drawHelmet() {
    drawPixel(color = Color.Black, topLeft = Offset(8f, 2f))
    drawRect(color = Color.Black, topLeft = Offset(2f, 3f), size = Size(8f, 5f))
    drawRect(color = Color.Black, topLeft = Offset(6f, 7f), size = Size(5f, 2f))
    drawPixel(color = Color.Black, topLeft = Offset(1f, 7f))

    drawRect(color = PowerUpColorGray, topLeft = Offset(3f, 2f), size = Size(5f, 4f))
    drawRect(color = PowerUpColorGray, topLeft = Offset(2f, 3f), size = Size(7f, 4f))
    drawHorizontalLine(color = Color.White, topLeft = Offset(3f, 2f), length = 3f)
    drawHorizontalLine(color = Color.White, topLeft = Offset(2f, 3f), length = 2f)
    drawPixel(color = Color.White, topLeft = Offset(2f, 4f))

    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(6f, 7f), length = 4f)
    drawPixel(color = PowerUpColorGray, topLeft = Offset(1f, 6f))
}

private fun PixelDrawScope.drawStar() {

}

private fun PixelDrawScope.drawGrenade() {

}

private fun PixelDrawScope.drawTank() {

}

private fun PixelDrawScope.drawShovel() {

}

private fun PixelDrawScope.drawTimer() {

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