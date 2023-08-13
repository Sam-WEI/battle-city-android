package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.tooling.preview.Preview
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun BrickTitle(vararg texts: String, modifier: Modifier) {
    val maxLen = texts.maxOf { it.length }
    val lines = texts.size

    val hGrid = maxLen * 2
    val vGrid = lines * 2 + (lines - 1) * 1 // 1 grid between lines

    val brickElements: Set<BrickElement> = remember(hGrid, vGrid) {
        mutableSetOf<BrickElement>().apply {
            val cols = BrickElement.granularity * hGrid
            val rows = BrickElement.granularity * vGrid
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    add(BrickElement(r, c, hGrid))
                }
            }
        }
    }

    // the masked bricks take up 12x5 grids.
    PixelTextPaintScope {
        val textPaint = LocalPixelFontPaint.current
        Grid(
            modifier = modifier.wrapContentSize(),
            gridSize = hGrid,
        ) {
            PixelCanvas(
                widthInMapPixel = hGrid.cell2mpx,
                heightInMapPixel = vGrid.cell2mpx,
            ) {
                brickElements.forEach { element ->
                    val offset = element.offsetInMapPixel
                    translate(offset.x, offset.y) {
                        this as PixelDrawScope
                        drawBrickElement(element, groutColor = Color.White)
                    }
                }
                // Each letter sits in a square that originally is as big as 1/2 * 1/2 whole brick.
                // For the brick title, each letter contains 2x2 whole bricks, resulting in a scale of 4.
                val scale = 4f
                scale(scale, pivot = Offset.Zero) {
                    this as PixelDrawScope
                    drawIntoCanvas {
                        it.withSaveLayer(
                            Rect(Offset.Zero, Size((hGrid / scale).cell2mpx, (vGrid / scale).cell2mpx)),
                            Paint().apply { blendMode = BlendMode.DstIn }
                        ) {
                            texts.forEachIndexed { i, line ->
                                val x = (maxLen - line.length) / 2f * 2 / scale
                                val y = i * 1.5f * 2 / scale
                                drawPixelText(line, Offset(x.cell2mpx, y.cell2mpx), textPaint)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BrickTitlePreview() {
    BattleCityTheme {
        BrickTitle("BATTLE CITY", modifier = Modifier)
    }
}