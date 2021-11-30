package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.BrickElement

private const val gridUnitNum = 16

@Composable
fun LandingScreen() {
    var selectionIndex: Int by remember { mutableStateOf(0) }
    Grid(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black),
        gridUnitNum = gridUnitNum,
    ) {
        PixelText(text = "I-    00 HI- 20000", charHeight = 0.5f.grid2mpx, topLeft = Offset(1f.grid2mpx, 1f.grid2mpx))

        BrickTitle(
            Modifier
                .offset(0f.grid2mpx.mpx2dp, 2f.grid2mpx.mpx2dp)
                .scale(6f / 8f))

        val menuItems = listOf("1 PLAYER", "2 PLAYERS", "STAGES")

        menuItems.forEachIndexed { i, text ->
            PixelText(
                text = text,
                charHeight = 0.5f.grid2mpx,
                topLeft = Offset(6.grid2mpx, (10 + i).grid2mpx),
                onClick = { selectionIndex = i })
        }

        PixelText(text = "© 1980 1985 NAMCO LTD.", charHeight = 0.5f.grid2mpx, topLeft = Offset(3.grid2mpx, (gridUnitNum - 2).grid2mpx))
        PixelText(text = "  ALL RIGHTS RESERVED", charHeight = 0.5f.grid2mpx, topLeft = Offset(3.grid2mpx, (gridUnitNum - 1).grid2mpx))

        PixelCanvas(
            topLeftInMapPixel = Offset(5.5f.grid2mpx, 9.7f.grid2mpx + selectionIndex.grid2mpx)
        ) {
            drawForDirection(Direction.Right) {
                this as PixelDrawScope
                drawPlayerTankLevel1(0, PlayerYellowPalette)
            }
        }
    }
}

@Composable
fun PixelText(
    text: String,
    charHeight: MapPixel,
    topLeft: Offset,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    onClick: () -> Unit = {},
) {
    val charHeightDp = charHeight.mpx2dp
    val fontScale = LocalDensity.current.fontScale
    val charHeightSp = (charHeightDp / fontScale).value.sp // factor out possible font scale
    Box(
        modifier = modifier
            .offset(topLeft.x.mpx2dp, topLeft.y.mpx2dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = charHeightSp,
        )
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
    val hGrid = 12
    val vGrid = 5
    // the masked bricks take up 12x5 grids.
    PixelTextPaintScope {
        val textPaint = LocalPixelFontPaint.current
        Grid(
            modifier = modifier.wrapContentSize(),
            gridUnitNum = hGrid,
        ) {
            PixelCanvas(
                widthInMapPixel = hGrid.grid2mpx,
                heightInMapPixel = vGrid.grid2mpx,
            ) {
                Bricks.forEach { element ->
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
                            Rect(Offset.Zero, Size((hGrid / scale).grid2mpx, (vGrid / scale).grid2mpx)),
                            Paint().apply { blendMode = BlendMode.DstIn }
                        ) {
                            drawPixelText("BATTLE", Offset.Zero, textPaint)
                            // CITY is shifted to the right for 1 letter that takes up 2 whole bricks;
                            // It's also shifted down 1.5 letter height, which is 3 whole bricks.
                            drawPixelText("CITY", Offset((2 / scale).grid2mpx, (3 / scale).grid2mpx), textPaint)
                        }
                    }
                }
            }
        }
    }
}