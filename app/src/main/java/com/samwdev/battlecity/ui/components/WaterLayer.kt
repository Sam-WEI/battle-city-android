package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.entity.TreeElement
import com.samwdev.battlecity.entity.WaterElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun WaterLayer(waters: Set<WaterElement>) {
    Framer(
        framesDef = listOf(700, 700),
        infinite = true,
    ) {
        waters.forEach { el ->
            WaterBlock(element = el, frame = LocalFramer.current)
        }
    }
}

private val colorWaterGleam = Color(172, 237, 237)
private val colorWater = Color(58, 58, 255)

@Composable
private fun WaterBlock(element: WaterElement, frame: Int) {
    PixelCanvas(
        topLeftInMapPixel = element.offsetInMapPixel,
        widthInMapPixel = WaterElement.elementSize,
        heightInMapPixel = WaterElement.elementSize,
    ) {
        val partSize = TreeElement.elementSize / 2
        repeat(4) { ith ->
            translate(
                left = (ith % 2).toFloat() * partSize,
                top = (ith / 2).toFloat() * partSize
            ) {
                this@PixelCanvas.drawSquare(
                    color = colorWater,
                    topLeft = Offset(0f, 0f),
                    side = partSize
                )
                if (frame == 0) {
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(5f, 0f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(0f, 2f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(1f, 3f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(4f, 3f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(3f, 4f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(5f, 4f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(1f, 6f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(2f, 7f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(6f, 7f))
                } else {
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(7f, 0f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(1f, 1f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(2f, 2f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(3f, 3f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(6f, 3f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(7f, 4f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(3f, 5f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(2f, 6f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(4f, 6f))
                    this@PixelCanvas.drawPixel(color = colorWaterGleam, topLeft = Offset(0f, 7f))
                }
            }
        }
    }
}

@Preview
@Composable
fun WaterPreview() {
    BattleCityTheme {
        Pixelate(modifier = Modifier.size(500.dp), sideBlockCount = 3) {
            WaterBlock(element = WaterElement(0), frame = 0)
            WaterBlock(element = WaterElement(2), frame = 1)
        }
    }
}