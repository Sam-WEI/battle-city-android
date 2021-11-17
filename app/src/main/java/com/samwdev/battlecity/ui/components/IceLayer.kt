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
import com.samwdev.battlecity.entity.IceElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun IceLayer(ices: List<IceElement>) {
    ices.forEach { el ->
        IceBlock(element = el)
    }
}

private val iceColorWhite = Color.White
private val iceColorGray = Color(173, 173, 173)
private val iceColorDarkGray = Color(99, 99, 99)

@Composable
private fun IceBlock(element: IceElement) {
    PixelCanvas(
        widthInMapPixel = IceElement.elementSize,
        heightInMapPixel = IceElement.elementSize,
        topLeftInMapPixel = element.offsetInMapPixel,
    ) {
        val partSize = IceElement.elementSize / 2
        repeat(4) { ith ->
            translate(
                left = (ith % 2).toFloat() * partSize,
                top = (ith / 2).toFloat() * partSize
            ) {
                drawRect(color = iceColorGray, topLeft = Offset(0f, 0f), size = Size(partSize, partSize))
                drawRect(color = iceColorDarkGray, topLeft = Offset(0f, 0f), size = Size(1f, 1f))
                drawRect(color = iceColorDarkGray, topLeft = Offset(4f, 0f), size = Size(1f, 1f))
                drawRect(color = iceColorDarkGray, topLeft = Offset(0f, 4f), size = Size(1f, 1f))
                drawRect(color = iceColorWhite, topLeft = Offset(3f, 0f), size = Size(1f, 1f))
                drawRect(color = iceColorWhite, topLeft = Offset(0f, 3f), size = Size(1f, 1f))

                for (i in 0 until partSize.toInt()) {
                    drawRect(
                        color = iceColorWhite,
                        topLeft = Offset(i.toFloat(), partSize - 1 - i),
                        size = Size(1f, 1f)
                    )
                }
                for (i in 1 until partSize.toInt()) {
                    drawRect(
                        color = iceColorDarkGray,
                        topLeft = Offset(i.toFloat(), partSize - i),
                        size = Size(1f, 1f)
                    )
                }
                for (i in 4 until partSize.toInt()) {
                    drawRect(
                        color = iceColorWhite,
                        topLeft = Offset(i.toFloat(), partSize + 3 - i),
                        size = Size(1f, 1f)
                    )
                }
                for (i in 5 until partSize.toInt()) {
                    drawRect(
                        color = iceColorDarkGray,
                        topLeft = Offset(i.toFloat(), partSize + 4 - i),
                        size = Size(1f, 1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun IcePreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp), sideBlockCount = 4) {
            IceBlock(element = IceElement(0))
            IceBlock(element = IceElement(1))
            IceBlock(element = IceElement(2))
        }
    }
}