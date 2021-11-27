package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.entity.IceElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun IceLayer(ices: Set<IceElement>) {
    ices.forEach { el ->
        IceBlock(element = el)
    }
}

private val iceColorWhite = Color.White
private val iceColorGray = Color(173, 173, 173)
private val iceColorDarkGray = Color(99, 99, 99)

@Composable
fun IceBlock(element: IceElement) {
    PixelCanvas(
        topLeftInMapPixel = element.offsetInMapPixel,
        widthInMapPixel = IceElement.elementSize,
        heightInMapPixel = IceElement.elementSize,
    ) {
        val partSize = IceElement.elementSize / 2
        repeat(4) { ith ->
            translate(
                left = (ith % 2).toFloat() * partSize,
                top = (ith / 2).toFloat() * partSize
            ) {
                this@PixelCanvas.drawSquare(color = iceColorGray, topLeft = Offset(0f, 0f), side = partSize)
                this@PixelCanvas.drawPixel(color = iceColorDarkGray, topLeft = Offset(0f, 0f))
                this@PixelCanvas.drawPixel(color = iceColorDarkGray, topLeft = Offset(4f, 0f))
                this@PixelCanvas.drawPixel(color = iceColorDarkGray, topLeft = Offset(0f, 4f))
                this@PixelCanvas.drawPixel(color = iceColorWhite, topLeft = Offset(3f, 0f))
                this@PixelCanvas.drawPixel(color = iceColorWhite, topLeft = Offset(0f, 3f))

                this@PixelCanvas.drawDiagonalLine(
                    color = iceColorWhite,
                    end1 = Offset(0f, 7f),
                    end2 = Offset(7f, 0f),
                )

                this@PixelCanvas.drawDiagonalLine(
                    color = iceColorDarkGray,
                    end1 = Offset(1f, 7f),
                    end2 = Offset(7f, 1f),
                )

                this@PixelCanvas.drawDiagonalLine(
                    color = iceColorWhite,
                    end1 = Offset(4f, 7f),
                    end2 = Offset(7f, 4f),
                )

                this@PixelCanvas.drawDiagonalLine(
                    color = iceColorDarkGray,
                    end1 = Offset(5f, 7f),
                    end2 = Offset(7f, 5f),
                )
            }
        }
    }
}

@Preview
@Composable
fun IcePreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp), gridUnitNumber = 4) {
            IceBlock(element = IceElement(0))
            IceBlock(element = IceElement(1))
            IceBlock(element = IceElement(2))
            IceBlock(element = IceElement(2, 2))
        }
    }
}