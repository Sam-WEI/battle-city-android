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
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val BrickColorGray = Color(88, 88, 88)
private val BrickColorLightBrown = Color(145, 65, 0)
private val BrickColorDarkBrown = Color(96, 12, 0)

@Composable
fun BrickLayer(bricks: Set<BrickElement>) {
    val drawIndex = LocalDebugConfig.current.showBrickIndex
    val gridUnitNumber = LocalGridUnitNumber.current
    PixelCanvas(
        widthInMapPixel = gridUnitNumber.first.grid2mpx,
        heightInMapPixel = gridUnitNumber.second.grid2mpx,
    ) {
        bricks.forEach { element ->
            val offset = element.offsetInMapPixel
            translate(offset.x, offset.y) {
                this as PixelDrawScope
                drawBrickElement(element, drawIndex)
            }
        }
    }
}

fun PixelDrawScope.drawBrickElement(
    element: BrickElement,
    drawIndex: Boolean = false,
    groutColor: Color = BrickColorGray,
) {
    drawRect(
        color = groutColor,
        size = Size(BrickElement.elementSize, BrickElement.elementSize)
    )
    if (element.patternIndex == 0) {
        drawHorizontalLine(
            color = BrickColorDarkBrown,
            topLeft = Offset(0f, 0f),
            length = BrickElement.elementSize,
        )
        drawRect(
            color = BrickColorLightBrown,
            topLeft = Offset(0f, 1f),
            size = Size(BrickElement.elementSize, 2f)
        )
    } else {
        drawSquare(
            color = BrickColorDarkBrown,
            topLeft = Offset(1f, 0f),
            side = 3f
        )
        drawSquare(
            color = BrickColorLightBrown,
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

@Preview
@Composable
fun BrickPreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp), gridUnitNum = 4) {
            val bricks = mutableSetOf<BrickElement>()
            for (r in 0 until 32) {
                for (c in 0 until 32) {
                    bricks.add(BrickElement.compose(r, c))
                }
            }
            BrickLayer(bricks = bricks)
        }
    }
}