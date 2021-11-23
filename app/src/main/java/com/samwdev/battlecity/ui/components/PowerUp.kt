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
fun FlashingPowerUp(topLeft: Offset, powerUp: PowerUp) {
    Framer(framesDef = listOf(150, 100), infinite = true) {
        if (LocalFramer.current == 0) {
            PowerUp(topLeft = topLeft, powerUp = powerUp)
        }
    }
}

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
        drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(1f, 1f), length = 13f)
        drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(1f, 1f), length = 12f)

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
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(8f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(2f, 3f), size = Size(8f, 5f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(6f, 7f), size = Size(5f, 2f))
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
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(6f, 0f))
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(7f, 1f), length = 2f)
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(11f, 3f), length = 2f)
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(10f, 4f), length = 2f)
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(9f, 5f), length = 2f)

    drawRect(color = PowerUpColorShadow, topLeft = Offset(1f, 9f), size = Size(2f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(3f, 8f), size = Size(2f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(5f, 7f), size = Size(2f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(7f, 8f), size = Size(2f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(9f, 7f), size = Size(2f, 4f))

    drawPixel(color = PowerUpColorWhite, topLeft = Offset(5f, 0f))
    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(0f, 3f), length = 11f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(1f, 4f), length = 9f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(2f, 5f), length = 7f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(2f, 6f), length = 7f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(1f, 7f), length = 4f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(1f, 8f), length = 3f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(1f, 9f), length = 1f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(6f, 7f), length = 4f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(7f, 8f), length = 3f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(9f, 9f), length = 1f)
    drawRect(color = PowerUpColorWhite, topLeft = Offset(4f, 1f), size = Size(3f, 5f))

    drawVerticalLine(color = PowerUpColorGray, topLeft = Offset(6f, 1f), length = 3f)
    drawVerticalLine(color = PowerUpColorGray, topLeft = Offset(5f, 3f), length = 2f)

    drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(3f, 5f), length = 2f)
    drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(2f, 6f), length = 2f)
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(1f, 8f))

    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(6f, 6f), length = 2f)
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(8f, 7f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(9f, 8f))

}

private fun PixelDrawScope.drawGrenade() {
    drawRect(color = PowerUpColorShadow, topLeft = Offset(6f, 0f), size = Size(3f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(7f, 1f), size = Size(3f, 7f))
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(10f, 2f), length = 5f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(2f, 4f), length = 6f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(2f, 6f), length = 6f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(4f, 8f), length = 6f)
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(6f, 9f))
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(3f, 10f), length = 3f)

    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(3f, 0f), length = 3f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(6f, 0f), length = 2f)
    drawPixel(color = PowerUpColorGray, topLeft = Offset(8f, 1f))
    drawVerticalLine(color = PowerUpColorGray, topLeft = Offset(9f, 2f), length = 5f)

    drawRect(color = PowerUpColorGray, topLeft = Offset(3f, 1f), size = Size(2f, 2f))
    drawRect(color = PowerUpColorGray, topLeft = Offset(4f, 2f), size = Size(2f, 2f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(3f, 1f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(2f, 2f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(4f, 3f))
    drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(1f, 3f), length = 5f)

    drawPixel(color = PowerUpColorGray, topLeft = Offset(1f, 4f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(1f, 6f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(2f, 3f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(2f, 5f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(2f, 7f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(2f, 8f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(3f, 4f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(3f, 6f))

    drawPixel(color = PowerUpColorWhite, topLeft = Offset(4f, 5f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(4f, 7f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(3f, 9f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(5f, 5f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(5f, 7f))
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(4f, 9f), length = 2f)

    drawPixel(color = PowerUpColorGray, topLeft = Offset(6f, 4f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(6f, 6f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(6f, 8f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(7f, 3f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(7f, 5f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(7f, 7f))
}

private fun PixelDrawScope.drawTank() {
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(1f, 3f), length = 4f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(4f, 6f), length = 6f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(4f, 7f), length = 6f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(1f, 8f), length = 7f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(2f, 9f), length = 9f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(3f, 10f), length = 7f)
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(8f, 1f))
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(9f, 2f), length = 2f)
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(10f, 4f), length = 2f)
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(11f, 6f), length = 3f)
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(10f, 9f))

    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(1f, 2f), length = 4f)
    drawRect(color = PowerUpColorGray, topLeft = Offset(5f, 1f), size = Size(3f, 3f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(5f, 1f))

    drawPixel(color = PowerUpColorWhite, topLeft = Offset(3f, 4f))
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(4f, 4f), length = 2f)
    drawPixel(color = PowerUpColorGray, topLeft = Offset(8f, 4f))

    drawPixel(color = PowerUpColorWhite, topLeft = Offset(2f, 5f))
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(3f, 5f), length = 5f)

    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(1f, 6f), length = 9f)
    drawHorizontalLine(color = PowerUpColorWhite, topLeft = Offset(3f, 6f), length = 4f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(1f, 7f), length = 3f)

    drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(2f, 7f), length = 2f)
    drawVerticalLine(color = PowerUpColorGray, topLeft = Offset(10f, 7f), length = 2f)
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(3f, 9f), length = 7f)

    drawPixel(color = PowerUpColorWhite, topLeft = Offset(4f, 8f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(6f, 8f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(8f, 8f))

}

private fun PixelDrawScope.drawShovel() {
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(8f, 3f), length = 3f)
    drawDiagonalLine(color = PowerUpColorShadow, end1 = Offset(8f, 3f), end2 = Offset(6f, 5f))
    drawDiagonalLine(color = PowerUpColorShadow, end1 = Offset(7f, 7f), end2 = Offset(5f, 9f))
    drawPixel(color = PowerUpColorShadow, topLeft = Offset(6f, 6f))
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(1f, 10f), length = 4f)

    drawVerticalLine(color = PowerUpColorWhite, topLeft = Offset(8f, 0f), length = 2f)
    drawPixel(color = PowerUpColorGray, topLeft = Offset(9f, 1f))
    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(8f, 2f), length = 3f)

    drawDiagonalLine(color = PowerUpColorWhite, end1 = Offset(5f, 5f), end2 = Offset(7f, 3f))
    drawDiagonalLine(color = PowerUpColorWhite, end1 = Offset(1f, 6f), end2 = Offset(3f, 4f))
    drawDiagonalLine(color = PowerUpColorWhite, end1 = Offset(1f, 7f), end2 = Offset(3f, 5f))
    drawDiagonalLine(color = PowerUpColorGray, end1 = Offset(1f, 8f), end2 = Offset(4f, 5f))
    drawDiagonalLine(color = PowerUpColorGray, end1 = Offset(1f, 9f), end2 = Offset(2f, 8f))
    drawDiagonalLine(color = PowerUpColorGray, end1 = Offset(2f, 9f), end2 = Offset(5f, 6f))
    drawDiagonalLine(color = PowerUpColorGray, end1 = Offset(3f, 9f), end2 = Offset(5f, 7f))
    drawDiagonalLine(color = PowerUpColorGray, end1 = Offset(4f, 9f), end2 = Offset(6f, 7f))
}

private fun PixelDrawScope.drawTimer() {
    drawRect(color = PowerUpColorShadow, topLeft = Offset(5f, 0f), size = Size(4f, 2f))
    drawRect(color = PowerUpColorShadow, topLeft = Offset(7f, 1f), size = Size(4f, 2f))
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(8f, 3f), length = 5f)
    drawVerticalLine(color = PowerUpColorShadow, topLeft = Offset(9f, 4f), length = 3f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(1f, 7f), length = 8f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(2f, 8f), length = 6f)
    drawHorizontalLine(color = PowerUpColorShadow, topLeft = Offset(3f, 9f), length = 4f)

    drawHorizontalLine(color = PowerUpColorGray, topLeft = Offset(4f, 0f), length = 4f)
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(4f, 0f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(6f, 0f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(8f, 1f))
    drawPixel(color = PowerUpColorGray, topLeft = Offset(9f, 1f))

    drawPixel(color = PowerUpColorGray, topLeft = Offset(4f, 1f))

    drawPixel(color = PowerUpColorGray, topLeft = Offset(9f, 1f))
    drawPixel(color = PowerUpColorWhite, topLeft = Offset(9f, 1f))

    drawRect(color = PowerUpColorGray, topLeft = Offset(3f, 2f), size = Size(4f, 7f))
    drawRect(color = PowerUpColorGray, topLeft = Offset(1f, 4f), size = Size(8f, 3f))
    drawRect(color = PowerUpColorGray, topLeft = Offset(2f, 3f), size = Size(6f, 5f))
    drawRect(color = PowerUpColorWhite, topLeft = Offset(3f, 3f), size = Size(4f, 5f))
    drawRect(color = PowerUpColorWhite, topLeft = Offset(2f, 4f), size = Size(6f, 3f))

    drawVerticalLine(color = PowerUpColorBlue, topLeft = Offset(4f, 4f), length = 2f)
    drawPixel(color = PowerUpColorBlue, topLeft = Offset(5f, 6f))
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