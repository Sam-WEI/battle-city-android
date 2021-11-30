package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.BrickElement

private const val gridUnitNum = 18

@Composable
fun LandingScreen() {
    PixelTextPaintScope {
        val textPaint = LocalPixelFontPaint.current.apply {
            color = android.graphics.Color.WHITE
        }
        Grid(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.Black),
            gridUnitNum = gridUnitNum,
        ) {
            PixelCanvas(
                Modifier.fillMaxSize()
            ) {
                drawPixelText("I-    00 HI- 20000", topLeft = Offset(1f.grid2mpx, 1f.grid2mpx), textPaint)

                translate(7.grid2mpx, 11.grid2mpx) {
                    this as PixelDrawScope
                    drawPixelText("1 PLAYER", topLeft = Offset(0f, 0f), textPaint)
                    drawPixelText("2 PLAYERS", topLeft = Offset(0f, 1.grid2mpx), textPaint)
                    drawPixelText("STAGES", topLeft = Offset(0f, 2.grid2mpx), textPaint)

                    translate(left = (-0.8f).grid2mpx, top = (-0.3f).grid2mpx) {
                        rotate(Direction.Right.degreeF, pivot = Offset.Zero) {

                            this as PixelDrawScope
                            drawPlayerTankLevel1(0, PlayerYellowPalette)
                        }
                    }
                }

                translate(3.grid2mpx, (gridUnitNum - 2).grid2mpx) {
                    this as PixelDrawScope
                    drawPixelText("© 1980 1985 NAMCO LTD.", topLeft = Offset(0f, 0f), textPaint)
                    drawPixelText("  ALL RIGHTS RESERVED", topLeft = Offset(0f, 1.grid2mpx), textPaint)
                }
            }

            BrickTitle(Modifier.offset(0f.grid2mpx.mpx2dp, 2f.grid2mpx.mpx2dp).scale(6f / 8f).background(Color.Red))

        }

    }
}

private val Bricks: Set<BrickElement> = mutableSetOf<BrickElement>().apply {
    val cols = BrickElement.granularity * 12 // half should be big enough after scaled
    val rows = BrickElement.granularity * 5
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            add(BrickElement(r, c, gridUnitNum))
        }
    }
}

@Composable
fun BrickTitle(modifier: Modifier) {
    val textPaint = LocalPixelFontPaint.current
    Grid(
        modifier = modifier
            .wrapContentSize(),
        gridUnitNum = 12,
    ) {
        PixelCanvas(
            widthInMapPixel = 12.grid2mpx,
            heightInMapPixel = 5.grid2mpx
        ) {
            Bricks.forEach { element ->
                val offset = element.offsetInMapPixel
                translate(offset.x, offset.y) {
                    this as PixelDrawScope
                    drawBrickElement(element, groutColor = Color.White)
                }
            }

            scale(4f, pivot = Offset.Zero) {
                this as PixelDrawScope
                drawIntoCanvas {
                    it.withSaveLayer(
                        Rect(Offset.Zero, Size(3f.grid2mpx, 1.25f.grid2mpx)), // 2 letters take up 1 grid width, so BATTLE takes up 3
                        Paint().apply { blendMode = BlendMode.DstIn }
                    ) {
                        drawPixelText("BATTLE", Offset.Zero, textPaint)
                        // CITY is shifted to the right for 1 letter that takes up 0.5 grid;
                        // It's also shifted down 1.5 letter height, where 1 letter height is also 0.5 grid.
                        drawPixelText("CITY", Offset(0.5f.grid2mpx, 0.75f.grid2mpx), textPaint)
                    }
                }
            }
        }
    }
}