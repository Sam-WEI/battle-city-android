package com.samwdev.battlecity.ui.components

import android.content.res.Resources
import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.DefaultStrokeLineMiter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.samwdev.battlecity.R
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.nextUp

@Composable
fun Hud(
    botCount: Int,
    lifeCount: Int,
    level: Int,
    hudLayoutOrientation: Orientation = Orientation.Horizontal,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .then(
                if (hudLayoutOrientation == Orientation.Horizontal) {
                    Modifier
                        .fillMaxWidth()
                        .height(1.grid2mpx.mpx2dp)
                } else {
                    Modifier
                        .fillMaxHeight()
                        .width(1.grid2mpx.mpx2dp)
                }
            )
    ) {
        LevelInfo(lifeCount, modifier = Modifier
            .align(Alignment.CenterStart)
            .background(Color.Green)
            .fillMaxWidth()
            .fillMaxHeight())
//        BotIcon(botCount, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun LevelInfo(lifeCount: Int, modifier: Modifier) {
    val context = LocalContext.current
    val tf = remember(context) {
        try { ResourcesCompat.getFont(context, R.font.pixel_font) }
        catch (e: Resources.NotFoundException) { Typeface.DEFAULT }
    }
    val textPaint = remember { Paint().asFrameworkPaint().apply {
        color = android.graphics.Color.BLACK
        textSize = 8f
        typeface = tf
    } }
    PixelCanvas {
        drawIntoCanvas {
            it.nativeCanvas.drawText("1P", 0f, 8f, textPaint)
        }
        translate(top = 8f) {
            this as PixelDrawScope
            drawPlayerLifeIcon()
        }
        translate(8f, 8f) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(lifeCount.toString(), 0f, 8f, textPaint)
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
        length = 2f,
    )
    drawVerticalLine(
        color = ColorOrange,
        topLeft = Offset(3f, 6f),
        length = 2f,
    )
    drawHorizontalLine(
        color = ColorOrange,
        topLeft = Offset(2f, 0f),
        length = 3f,
    )
    translate(left = 0.5f, top = 0.5f) {
        drawPath(
            Path().apply {
                moveTo(1f, 3f)
                lineTo(2f, 3f)
                lineTo(2f, 2f)
                lineTo(4f, 2f)
                lineTo(4f, 3f)
                lineTo(5f, 3f)
                lineTo(5f, 5f)
                lineTo(4f, 5f)
                lineTo(4f, 6f)
                lineTo(2f, 6f)
                lineTo(2f, 5f)
                lineTo(1f, 5f)
                close()
            },
            color = ColorOrange,
            style = Stroke(width = 1f),
        )
    }

//    drawRect(
//        color = ColorOrange,
//        topLeft = Offset(2f, 2f),
//        size = Size(3f, 5f),
//        style = Stroke(width = 1f, cap = StrokeCap.Butt, join = StrokeJoin.Miter, miter = 10f)
//    )
//    drawRect(
//        color = ColorOrange,
//        topLeft = Offset(1f, 3f),
//        size = Size(5f, 3f),
//        style = Stroke(width = 1f, cap = StrokeCap.Square)
//    )
}

@Composable
fun BotIcon(botCount: Int, modifier: Modifier) {
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