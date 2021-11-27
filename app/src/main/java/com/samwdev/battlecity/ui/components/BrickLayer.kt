package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun BrickLayer(bricks: Set<BrickElement>) {
    bricks.forEach { el ->
        BrickBlock(element = el)
    }
}

private val brickColorGray = Color(88, 88, 88)
private val brickColorLightBrown = Color(145, 65, 0)
private val brickColorDarkBrown = Color(96, 12, 0)

@Composable
private fun BrickBlock(element: BrickElement) {
    val drawIndex = LocalDebugConfig.current.showBrickIndex
    PixelCanvas(
        topLeftInMapPixel = element.offsetInMapPixel,
        widthInMapPixel = BrickElement.elementSize,
        heightInMapPixel = BrickElement.elementSize,
    ) {
        drawRect(
            color = brickColorGray,
            size = Size(BrickElement.elementSize, BrickElement.elementSize)
        )
        if (element.patternIndex == 0) {
            drawHorizontalLine(
                color = brickColorDarkBrown,
                topLeft = Offset(0f, 0f),
                length = BrickElement.elementSize,
            )
            drawRect(
                color = brickColorLightBrown,
                topLeft = Offset(0f, 1f),
                size = Size(BrickElement.elementSize, 2f)
            )
        } else {
            drawSquare(
                color = brickColorDarkBrown,
                topLeft = Offset(1f, 0f),
                side = 3f
            )
            drawSquare(
                color = brickColorLightBrown,
                topLeft = Offset(2f, 1f),
                side = 2f
            )
        }

        if (drawIndex) {
            drawText(
                "${element.index}",
                color = Color.White,
                offset = Offset(0f, 2f),
                fontSize = 0.6.sp
            )
        }
    }
}

@Preview
@Composable
fun BrickPreview() {
    BattleCityTheme {
        Pixelate(modifier = Modifier.size(500.dp), sideBlockCount = 3) {
            for (r in 0 until 8) {
                for (c in 0 until 8) {
                    BrickBlock(element = BrickElement(r * BrickElement.countInOneLine + c))
                }
            }
        }
    }
}