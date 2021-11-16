package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.PixelCanvas
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun BrickLayer(bricks: List<BrickElement>) {
    bricks.forEach { el ->
        BrickBlock(element = el)
    }
}

private val brickColorGray = Color(88, 88, 88)
private val brickColorLightBrown = Color(145, 65, 0)
private val brickColorDarkBrown = Color(96, 12, 0)

@Composable
private fun BrickBlock(element: BrickElement) {
    PixelCanvas(
        widthInMapPixel = BrickElement.elementSize,
        heightInMapPixel = BrickElement.elementSize,
        topLeftInMapPixel = element.offsetInMapPixel,
    ) {
        drawRect(
            color = brickColorGray,
            size = Size(BrickElement.elementSize, BrickElement.elementSize)
        )
        if (element.patternIndex == 0) {
            drawRect(
                color = brickColorDarkBrown,
                topLeft = Offset(0f, 0f),
                size = Size(BrickElement.elementSize, 1f)
            )
            drawRect(
                color = brickColorLightBrown,
                topLeft = Offset(0f, 1f),
                size = Size(BrickElement.elementSize, 2f)
            )
        } else {
            drawRect(
                color = brickColorDarkBrown,
                topLeft = Offset(1f, 0f),
                size = Size(3f, 3f)
            )
            drawRect(
                color = brickColorLightBrown,
                topLeft = Offset(2f, 1f),
                size = Size(2f, 2f)
            )
        }
    }
}

@Preview
@Composable
fun BrickPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp)) {
            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    BrickBlock(element = BrickElement(r * BrickElement.countInOneLine + c))
                }
            }
        }
    }
}