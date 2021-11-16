package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.core.PixelCanvas
import com.samwdev.battlecity.entity.TreeElement
import com.samwdev.battlecity.entity.WaterElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlinx.coroutines.delay


@Composable
fun WaterLayer(mapState: MapState) {
    var frame by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            frame = (frame + 1) % 2
            delay(500)
        }
    }
    mapState.waters.forEach { el ->
        WaterBlock(element = el, frame = frame)
    }
}

private val colorWaterGleam = Color(172, 237, 237)
private val colorWater = Color(58, 58, 255)

@Composable
private fun WaterBlock(element: WaterElement, frame: Int) {
    PixelCanvas(
        widthInMapPixel = WaterElement.elementSize,
        heightInMapPixel = WaterElement.elementSize,
        topLeftInMapPixel = element.offsetInMapPixel,
    ) {
        val partSize = TreeElement.elementSize / 2
        repeat(4) { ith ->
            translate(
                left = (ith % 2).toFloat() * partSize,
                top = (ith / 2).toFloat() * partSize
            ) {
                drawRect(
                    color = colorWater,
                    size = Size(partSize, partSize)
                )
                if (frame == 0) {
                    drawRect(color = colorWaterGleam, topLeft = Offset(5f, 0f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(0f, 2f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(1f, 3f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(4f, 3f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(3f, 4f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(5f, 4f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(1f, 6f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(2f, 7f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(6f, 7f), size = Size(1f, 1f))
                } else {
                    drawRect(color = colorWaterGleam, topLeft = Offset(7f, 0f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(1f, 1f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(2f, 2f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(3f, 3f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(6f, 3f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(7f, 4f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(3f, 5f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(2f, 6f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(4f, 6f), size = Size(1f, 1f))
                    drawRect(color = colorWaterGleam, topLeft = Offset(0f, 7f), size = Size(1f, 1f))
                }
            }
        }
    }
}

@Preview
@Composable
fun WaterPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp)) {
            WaterBlock(element = WaterElement(0), frame = 0)
            WaterBlock(element = WaterElement(2), frame = 1)
        }
    }
}