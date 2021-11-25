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
import com.samwdev.battlecity.entity.TreeElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val TreeColorDark = Color(12, 65, 0)
private val TreeColorLight = Color(129, 208, 0)

@Composable
fun TreeLayer(trees: Set<TreeElement>) {
    trees.forEach { el ->
        TreeBlock(element = el)
    }
}

@Composable
private fun TreeBlock(element: TreeElement) {
    PixelCanvas(
        topLeftInMapPixel = element.offsetInMapPixel,
        widthInMapPixel = TreeElement.elementSize,
        heightInMapPixel = TreeElement.elementSize,
    ) {
        val partSize = TreeElement.elementSize / 2
        repeat(4) { ith ->
            translate(
                left = (ith % 2).toFloat() * partSize,
                top = (ith / 2).toFloat() * partSize
            ) {
                this as PixelDrawScope
                // base color
                drawRect(
                    color = TreeColorLight,
                    topLeft = Offset(1f, 0f),
                    size = Size(6f, 8f),
                )
                drawRect(
                    color = TreeColorLight,
                    topLeft = Offset(0f, 1f),
                    size = Size(8f, 6f),
                )

                // shadows
                drawRect(
                    color = TreeColorDark,
                    topLeft = Offset(1f, 0f),
                    size = Size(5f, 1f),
                )
                drawRect(
                    color = TreeColorDark,
                    topLeft = Offset(0f, 1f),
                    size = Size(3f, 3f),
                )
                drawPixel(
                    color = TreeColorDark,
                    topLeft = Offset(4f, 1f),
                )
                drawRect(
                    color = TreeColorDark,
                    topLeft = Offset(3f, 2f),
                    size = Size(2f, 1f),
                )
                drawPixel(
                    color = TreeColorDark,
                    topLeft = Offset(6f, 1f),
                )
                drawRect(
                    color = TreeColorDark,
                    topLeft = Offset(5f, 3f),
                    size = Size(2f, 1f),
                )
                drawRect(
                    color = TreeColorDark,
                    topLeft = Offset(0f, 4f),
                    size = Size(2f, 1f),
                )
                drawPixel(
                    color = TreeColorDark,
                    topLeft = Offset(3f, 4f),
                )
                drawPixel(
                    color = TreeColorDark,
                    topLeft = Offset(7f, 4f),
                )
                drawRect(
                    color = TreeColorDark,
                    topLeft = Offset(0f, 5f),
                    size = Size(3f, 1f),
                )
                drawPixel(
                    color = TreeColorDark,
                    topLeft = Offset(5f, 6f),
                )
                drawPixel(
                    color = TreeColorDark,
                    topLeft = Offset(3f, 7f),
                )
            }
        }
    }
}

@Preview
@Composable
fun TreePreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp), sideBlockCount = 2) {
            TreeBlock(element = TreeElement(0))
            TreeBlock(element = TreeElement(1))
        }
    }
}