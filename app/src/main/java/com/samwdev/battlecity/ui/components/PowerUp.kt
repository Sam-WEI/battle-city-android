package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.PowerUp
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val PowerUpColorBlue = Color(43, 71, 121)
private val PowerUpColorGray = Color(181, 181, 181)
private val PowerUpColorShadow = Color.Black
private val PowerUpColorWhite = Color.White

@Composable
fun PowerUp(topLeft: Offset, powerUp: PowerUp) {
    PixelCanvas(
        topLeftInMapPixel = topLeft,
        widthInMapPixel = 1f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
    ) {
        drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(1f, 0f), length = 13f)
        drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(0f, 1f), length = 12f)
        drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(14f, 1f), length = 12f)
        drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(1f, 13f), length = 13f)
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
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(8f, 2f), blendMode = BlendMode.DstOut)
    drawRect(color = PowerUpColorShadow, topLeft = Offset(2f, 3f), size = Size(8f, 5f), blendMode = BlendMode.DstOut)
    drawRect(color = PowerUpColorShadow, topLeft = Offset(6f, 7f), size = Size(5f, 2f), blendMode = BlendMode.DstOut)
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(1f, 7f), blendMode = BlendMode.DstOut)

    drawRect(color = PowerUpColorGray, topLeft = Offset(3f, 2f), size = Size(5f, 4f))
    drawRect(color = PowerUpColorGray, topLeft = Offset(2f, 3f), size = Size(7f, 4f))
    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(3f, 2f), length = 3f)
    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(2f, 3f), length = 2f)
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(2f, 4f))

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
        Map(modifier = Modifier.size(500.dp), sideBlockCount = 6) {
            PowerUp.values().forEachIndexed { index, powerUp ->
                PowerUp(topLeft = Offset(0f.grid2mpx, index.toFloat().grid2mpx), powerUp)
            }
        }
    }
}