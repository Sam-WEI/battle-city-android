package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.nextUp

@Composable
fun Hud(
    botCount: Int,
    lifeCount: Int,
    level: Int,
    modifier: Modifier = Modifier,
    hudLayoutOrientation: Orientation = Orientation.Horizontal,
) {
    Grid(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
        PixelTextPaintScope {
            PlayerInfo(
                lifeCount,
                modifier = Modifier
                    .size(1.5f.grid2mpx.mpx2dp, 1.grid2mpx.mpx2dp)
                    .offset(x = 0.5f.grid2mpx.mpx2dp, y = 0f.mpx2dp)
            )

            LevelInfo(
                level = level,
                modifier = Modifier
                    .size(2f.grid2mpx.mpx2dp, 1.grid2mpx.mpx2dp)
                    .offset(x = 4.5f.grid2mpx.mpx2dp, y = 0f.mpx2dp)
            )
        }

        BotIcons(
            botCount,
            modifier = Modifier
                .size(6.grid2mpx.mpx2dp, 1.grid2mpx.mpx2dp)
                .offset(x = 7f.grid2mpx.mpx2dp, y = 0f.mpx2dp)
        )
    }
}

@Composable
fun PlayerInfo(lifeCount: Int, modifier: Modifier) {
    val textPaint = LocalPixelFontPaint.current.apply {
        textSize = 8f
    }
    PixelCanvas(modifier) {
        scale(0.7f, Offset(0f, 0.5f.grid2mpx)) {
            this as PixelDrawScope
            drawPixelText("1P", Offset.Zero, textPaint)
            translate(top = 8f) {
                this as PixelDrawScope
                drawPlayerLifeIcon()
            }
            translate(8f, 8f) {
                this as PixelDrawScope
                drawPixelText(lifeCount.toString(), Offset.Zero, textPaint)
            }
        }
    }
}

private val ColorOrange = Color(145, 79, 26)

private fun PixelDrawScope.drawPlayerLifeIcon() {
    drawVerticalLine(
        color = ColorOrange,
        topLeft = Offset(0f, 1f),
        length = 7f,
    )
    drawVerticalLine(
        color = ColorOrange,
        topLeft = Offset(6f, 1f),
        length = 7f,
    )
    drawVerticalLine(
        color = ColorOrange,
        topLeft = Offset(3f, 0f),
        length = 8f,
    )
    drawHorizontalLine(
        color = ColorOrange,
        topLeft = Offset(2f, 0f),
        length = 3f,
    )
//    translate(left = 0.5f, top = 0.5f) {
//        drawPath(
//            Path().apply {
//                moveTo(1f, 3f)
//                lineTo(2f, 3f)
//                lineTo(2f, 2f)
//                lineTo(4f, 2f)
//                lineTo(4f, 3f)
//                lineTo(5f, 3f)
//                lineTo(5f, 5f)
//                lineTo(4f, 5f)
//                lineTo(4f, 6f)
//                lineTo(2f, 6f)
//                lineTo(2f, 5f)
//                lineTo(1f, 5f)
//                close()
//            },
//            color = ColorOrange,
//            style = Stroke(width = 1f),
//        )
//    }

    drawRect(
        color = ColorOrange,
        topLeft = Offset(2f, 2f),
        size = Size(3f, 5f),
    )
    drawRect(
        color = ColorOrange,
        topLeft = Offset(1f, 3f),
        size = Size(5f, 3f),
    )
    drawIntoCanvas {
        it.withSaveLayer(
            Rect(Offset(2f, 3f), Size(3f, 3f)),
            Paint().apply { blendMode = BlendMode.DstOut }
        ) {
            drawRect(Color.White, Offset(3f, 3f), Size(1f, 3f))
            drawRect(Color.White, Offset(2f, 4f), Size(3f, 1f))
        }

        it.withSaveLayer(
            Rect(Offset(2f, 3f), Size(3f, 3f)),
            Paint().apply { blendMode = BlendMode.DstAtop }
        ) {
            // todo make the cross transparent
            drawRect(Color.Gray, Offset(2f, 3f), Size(3f, 3f))
        }
    }
}

@Composable
private fun LevelInfo(level: Int, modifier: Modifier) {
    val textPaint = LocalPixelFontPaint.current
    PixelCanvas(modifier) {
        scale(0.7f, Offset(0f, 0.5f.grid2mpx)) {
            this as PixelDrawScope
            drawRect(color = Color.Black, topLeft = Offset(0f, 0f), size = Size(2f, 16f))
            drawDiagonalLine(color = ColorOrange, end1 = Offset(2f, 1f), end2 = Offset(15f, 7f))
            drawDiagonalLine(color = ColorOrange, end1 = Offset(2f, 2f), end2 = Offset(15f, 8f))
            drawDiagonalLine(color = ColorOrange, end1 = Offset(2f, 3f), end2 = Offset(13f, 8f))
            drawDiagonalLine(color = ColorOrange, end1 = Offset(2f, 4f), end2 = Offset(11f, 8f))
            drawRect(color = ColorOrange, topLeft = Offset(2f, 5f), size = Size(8f, 4f))
            translate(1f.grid2mpx, 0.5f.grid2mpx) {
                this as PixelDrawScope
                drawPixelText(level.toString(), Offset.Zero, textPaint)
            }
        }
    }
}

@Composable
fun BotIcons(botCount: Int, modifier: Modifier) {
    val rowMax = if (botCount <= 20) 10 else 20
    PixelCanvas(
        widthInMapPixel = 5f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
        modifier = modifier,
    ) {
        val topPadding = (4 - (botCount / rowMax.toFloat()).nextUp()) / 4f
        scale(0.5f, pivot = Offset(0f, topPadding.grid2mpx)) {
            repeat(botCount) { i ->
                val row = i / rowMax
                val col = i % rowMax
                translate(left = col * 0.5f.grid2mpx, top = row * 0.5f.grid2mpx) {
                    this as PixelDrawScope
                    drawVerticalLine(
                        color = Color.Black,
                        topLeft = Offset(0f, 0f),
                        length = 6f
                    )
                    drawVerticalLine(
                        color = Color.Black,
                        topLeft = Offset(3f, 0f),
                        length = 6f
                    )
                    drawVerticalLine(
                        color = Color.Black,
                        topLeft = Offset(6f, 0f),
                        length = 6f
                    )
                    drawHorizontalLine(
                        color = Color.Black,
                        topLeft = Offset(2f, 6f),
                        length = 3f
                    )
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(1f, 2f),
                        size = Size(5f, 2f)
                    )
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(2f, 1f),
                        size = Size(3f, 4f)
                    )
                    drawVerticalLine(
                        color = Color(97, 20, 9),
                        topLeft = Offset(3f, 2f),
                        length = 2f
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HudPreview() {
    BattleCityTheme {
        Grid(
            modifier = Modifier
                .width(500.dp)
                .height(200.dp)
                .background(Color(117, 117, 117)),
            gridUnitNum = MAP_BLOCK_COUNT
        ) {
            Hud(botCount = 20, lifeCount = 3, level = 30)
        }
    }
}