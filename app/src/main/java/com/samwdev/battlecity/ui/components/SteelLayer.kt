package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun SteelLayer(steels: List<SteelElement>) {
    steels.forEach { el ->
        SteelBlock(element = el)
    }
}

private val steelColorCenter = Color.White
private val steelColorLight = Color(163, 163, 163)
private val steelColorDark = Color(88, 88, 88)

@Composable
private fun SteelBlock(element: SteelElement) {
    PixelCanvas(
        widthInMapPixel = SteelElement.elementSize,
        heightInMapPixel = SteelElement.elementSize,
        topLeftInMapPixel = element.offsetInMapPixel,
    ) {
        drawSquare(
            color = steelColorLight,
            topLeft = Offset(0f, 0f),
            side = SteelElement.elementSize,
        )
        drawSquare(
            color = steelColorDark,
            topLeft = Offset(2f, 2f),
            side = 6f,
        )
        drawPixel(
            color = steelColorDark,
            topLeft = Offset(7f, 1f),
        )
        drawPixel(
            color = steelColorDark,
            topLeft = Offset(1f, 7f),
        )
        drawSquare(
            color = steelColorCenter,
            topLeft = Offset(2f, 2f),
            side = 4f,
        )
    }
}

@Preview
@Composable
fun SteelPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp), sideBlockCount = 2) {
            SteelBlock(element = SteelElement(0))
            SteelBlock(element = SteelElement(1))
            SteelBlock(element = SteelElement(2))
            SteelBlock(element = SteelElement(3))
        }
    }
}