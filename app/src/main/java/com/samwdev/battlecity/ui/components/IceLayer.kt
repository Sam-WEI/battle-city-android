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
import com.samwdev.battlecity.entity.IceElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun IceLayer(ices: Set<IceElement>) {
    val gridSize = LocalGridSize.current
    PixelCanvas(
        widthInMapPixel = gridSize.first.cell2mpx,
        heightInMapPixel = gridSize.second.cell2mpx,
    ) {
        ices.forEach { element ->
            val offset = element.offsetInMapPixel
            translate(offset.x, offset.y) {
                this as PixelDrawScope
                drawIceElement()
            }
        }
    }
}

private val iceColorWhite = Color.White
private val iceColorGray = Color(173, 173, 173)
private val iceColorDarkGray = Color(99, 99, 99)

private fun PixelDrawScope.drawIceElement() {
    val partSize = IceElement.elementSize / 2
    repeat(4) { ith ->
        translate(
            left = (ith % 2).toFloat() * partSize,
            top = (ith / 2).toFloat() * partSize
        ) {
            this as PixelDrawScope
            drawSquare(color = iceColorGray, topLeft = Offset(0f, 0f), side = partSize)
            drawPixel(color = iceColorDarkGray, topLeft = Offset(0f, 0f))
            drawPixel(color = iceColorDarkGray, topLeft = Offset(4f, 0f))
            drawPixel(color = iceColorDarkGray, topLeft = Offset(0f, 4f))
            drawPixel(color = iceColorWhite, topLeft = Offset(3f, 0f))
            drawPixel(color = iceColorWhite, topLeft = Offset(0f, 3f))

            drawDiagonalLine(
                color = iceColorWhite,
                end1 = Offset(0f, 7f),
                end2 = Offset(7f, 0f),
            )

            drawDiagonalLine(
                color = iceColorDarkGray,
                end1 = Offset(1f, 7f),
                end2 = Offset(7f, 1f),
            )

            drawDiagonalLine(
                color = iceColorWhite,
                end1 = Offset(4f, 7f),
                end2 = Offset(7f, 4f),
            )

            drawDiagonalLine(
                color = iceColorDarkGray,
                end1 = Offset(5f, 7f),
                end2 = Offset(7f, 5f),
            )
        }
    }
}

@Preview
@Composable
fun IcePreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp), gridSize = 3) {
            IceLayer(ices = setOf(
                IceElement.compose(0, 0),
                IceElement.compose(0, 1),
                IceElement.compose(0, 2),
                IceElement.compose(1, 0),
                IceElement.compose(1, 1),
                IceElement.compose(1, 2),
            ))
        }
    }
}