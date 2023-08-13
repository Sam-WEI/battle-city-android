package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun SteelLayer(steels: Set<SteelElement>) {
    val drawIndex = LocalDebugConfig.current.showElementIndex
    val gridSize = LocalGridSize.current
    PixelCanvas(
        widthInMapPixel = gridSize.first.cell2mpx,
        heightInMapPixel = gridSize.second.cell2mpx,
    ) {
        steels.forEach { element ->
            val offset = element.offsetInMapPixel
            translate(offset.x, offset.y) {
                this as PixelDrawScope
                drawSteelElement(element, drawIndex)
            }
        }
    }
}

private val SteelColorCenter = Color.White
private val SteelColorLight = Color(163, 163, 163)
private val SteelColorDark = Color(88, 88, 88)

private fun PixelDrawScope.drawSteelElement(element: SteelElement, drawIndex: Boolean) {
    drawSquare(
        color = SteelColorLight,
        topLeft = Offset(0f, 0f),
        side = SteelElement.elementSize,
    )
    drawSquare(
        color = SteelColorDark,
        topLeft = Offset(2f, 2f),
        side = 6f,
    )
    drawPixel(
        color = SteelColorDark,
        topLeft = Offset(7f, 1f),
    )
    drawPixel(
        color = SteelColorDark,
        topLeft = Offset(1f, 7f),
    )
    drawSquare(
        color = SteelColorCenter,
        topLeft = Offset(2f, 2f),
        side = 4f,
    )
    if (drawIndex) {
        drawText(
            "${element.index}",
            color = Color.Magenta,
            offset = Offset(0f, 5f),
            fontSize = 1.3.sp
        )
    }
}

@Preview
@Composable
fun SteelPreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp), gridSize = 2) {
            val steels = mutableSetOf<SteelElement>()
            for (r in 0 until 4) {
                for (c in 0 until 4) {
                    steels.add(SteelElement.compose(r, c))
                }
            }
            SteelLayer(steels = steels)
        }
    }
}