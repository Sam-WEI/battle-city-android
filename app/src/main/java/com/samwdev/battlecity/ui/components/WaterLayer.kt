package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.entity.TreeElement
import com.samwdev.battlecity.entity.WaterElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun WaterLayer(waters: Set<WaterElement>) {
    Framer(
        framesDef = listOf(700, 700),
        infinite = true,
    ) {
        val frame = LocalFramer.current
        WaterLayerFrame(waters = waters, frame = frame)
    }
}

@Composable
fun WaterLayerFrame(waters: Set<WaterElement>, frame: Int) {
    val gridSize = LocalGridSize.current
    PixelCanvas(
        widthInMapPixel = gridSize.first.cell2mpx,
        heightInMapPixel = gridSize.second.cell2mpx,
    ) {
        waters.forEach { element ->
            val offset = element.offsetInMapPixel
            translate(offset.x, offset.y) {
                this as PixelDrawScope
                drawWaterElement(frame)
            }
        }
    }
}

private val colorWaterGleam = Color(172, 237, 237)
private val colorWater = Color(58, 58, 255)

private fun PixelDrawScope.drawWaterElement(frame: Int) {
    val partSize = TreeElement.elementSize / 2
    repeat(4) { ith ->
        translate(
            left = (ith % 2).toFloat() * partSize,
            top = (ith / 2).toFloat() * partSize
        ) {
            this as PixelDrawScope
            drawSquare(
                color = colorWater,
                topLeft = Offset(0f, 0f),
                side = partSize
            )
            if (frame == 0) {
                drawPixel(color = colorWaterGleam, topLeft = Offset(5f, 0f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(0f, 2f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(1f, 3f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(4f, 3f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(3f, 4f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(5f, 4f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(1f, 6f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(2f, 7f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(6f, 7f))
            } else {
                drawPixel(color = colorWaterGleam, topLeft = Offset(7f, 0f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(1f, 1f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(2f, 2f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(3f, 3f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(6f, 3f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(7f, 4f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(3f, 5f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(2f, 6f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(4f, 6f))
                drawPixel(color = colorWaterGleam, topLeft = Offset(0f, 7f))
            }
        }
    }
}

@Preview
@Composable
private fun WaterPreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp, 500.dp), gridSize = 3) {
            WaterLayerFrame(waters = setOf(WaterElement.compose(0, 0)), frame = 0)
            WaterLayerFrame(waters = setOf(WaterElement.compose(0, 2)), frame = 1)
        }
    }
}