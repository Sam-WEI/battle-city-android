package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.PixelCanvas
import com.samwdev.battlecity.core.PixelDrawScope
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val eagleColor = Color(88, 88, 88)
private val eagleColorEye = Color(96, 12, 0)

@Composable
fun EagleLayer(eagleElement: EagleElement) {
    PixelCanvas(
        widthInMapPixel = eagleElement.elementSize,
        heightInMapPixel = eagleElement.elementSize,
        topLeftInMapPixel = eagleElement.offsetInMapPixel,
    ) {
        drawHalfEagle()
        scale(scaleX = -1f, scaleY = 1f, pivot = Offset(eagleElement.elementSize / 2, 0f)) {
            this@PixelCanvas.drawHalfEagle()
        }
        drawPixelPoint(color = eagleColor, topLeft = Offset(6f, 2f))
        drawPixelPoint(color = eagleColor, topLeft = Offset(9f, 3f))
        drawPixelPoint(color = eagleColorEye, topLeft = Offset(8f, 3f))
    }
}

private fun PixelDrawScope.drawHalfEagle() {
    drawRect(color = eagleColor, topLeft = Offset(7f, 2f), size = Size(1f, 13f))
    drawRect(color = eagleColor, topLeft = Offset(0f, 1f), size = Size(2f, 1f))
    drawRect(color = eagleColor, topLeft = Offset(1f, 2f), size = Size(2f, 1f))
    drawRect(color = eagleColor, topLeft = Offset(0f, 3f), size = Size(4f, 1f))
    drawRect(color = eagleColor, topLeft = Offset(1f, 4f), size = Size(3f, 1f))
    drawRect(color = eagleColor, topLeft = Offset(0f, 5f), size = Size(6f, 1f))
    drawRect(color = eagleColor, topLeft = Offset(2f, 6f), size = Size(5f, 4f))
    drawRect(color = eagleColor, topLeft = Offset(1f, 7f), size = Size(1f, 1f))
    drawRect(color = eagleColor, topLeft = Offset(3f, 10f), size = Size(3f, 1f))
    // tail
    drawRect(color = eagleColor, topLeft = Offset(6f, 12f), size = Size(1f, 2f))
    drawRect(color = eagleColor, topLeft = Offset(4f, 13f), size = Size(2f, 2f))
    // red dots
    drawPixelPoint(color = eagleColorEye, topLeft = Offset(3f, 6f))
    drawPixelPoint(color = eagleColorEye, topLeft = Offset(4f, 7f))
    drawPixelPoint(color = eagleColorEye, topLeft = Offset(6f, 8f))
}

@Preview
@Composable
fun EaglePreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp), sideBlockCount = 3) {
            EagleLayer(eagleElement = EagleElement(1))
        }
    }
}