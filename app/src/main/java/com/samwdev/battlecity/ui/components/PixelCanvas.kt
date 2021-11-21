package com.samwdev.battlecity.ui.components

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * This canvas allows you to draw in original game pixel
 */
@Composable
fun PixelCanvas(
    modifier: Modifier = Modifier,
    topLeftInMapPixel: Offset = Offset.Zero,
    widthInMapPixel: MapPixel = 1f.grid2mpx,
    heightInMapPixel: MapPixel = 1f.grid2mpx,
    onDraw: PixelDrawScope.() -> Unit
) {
    val mpxDp = LocalMapPixelDp.current
    Canvas(
        modifier = Modifier
            .size(widthInMapPixel.mpx2dp, heightInMapPixel.mpx2dp)
            .offset(topLeftInMapPixel.x.mpx2dp, topLeftInMapPixel.y.mpx2dp)
            .then(modifier)
    ) {
        scale(mpxDp.toPx(), pivot = Offset.Zero) {
            PixelDrawScope(this).onDraw()
        }
    }
}

class PixelDrawScope(private val drawScope: DrawScope) : DrawScope by drawScope {
    private val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 12.sp.toPx()
        color = android.graphics.Color.BLUE
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    fun drawPixel(color: Color, topLeft: Offset) {
        drawRect(color = color, topLeft = topLeft, size = Size(1f, 1f))
    }

    fun drawVerticalLine(color: Color, topLeft: Offset, length: MapPixel) {
        drawRect(color = color, topLeft = topLeft, size = Size(1f, length))
    }

    fun drawHorizontalLine(color: Color, topLeft: Offset, length: MapPixel) {
        drawRect(color = color, topLeft = topLeft, size = Size(length, 1f))
    }

    fun drawSquare(color: Color, topLeft: Offset, side: MapPixel) {
        drawRect(color = color, topLeft = topLeft, size = Size(side, side))
    }

    fun drawDiagonalLine(color: Color, end1: Offset, end2: Offset) {
        val left = min(end1.x, end2.x)
        val right = max(end1.x, end2.x)
        val top = min(end1.y, end2.y)
        val bottom = max(end1.y, end2.y)

        val diffX = right - left
        val diffY = bottom - top
        if (diffX > diffY) {
            val k = (end1.y - end2.y) / (end1.x - end2.x)
            val b = end2.y - k * end2.x
            for (x in left.toInt()..right.toInt()) {
                val y = (x * k + b).roundToInt().toFloat()
                drawPixel(color = color, topLeft = Offset(x.toFloat(), y))
            }
        } else {
            val k = (end1.x - end2.x) / (end1.y - end2.y)
            val b = end2.x - k * end2.y
            for (y in top.toInt()..bottom.toInt()) {
                val x = (y * k + b).roundToInt().toFloat()
                drawPixel(color = color, topLeft = Offset(x, y.toFloat()))
            }
        }
    }

    fun drawText(text: String, color: Color, offset: Offset, fontSize: TextUnit) {
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                text,
                offset.x,
                offset.y,
                textPaint.apply {
                    this.color = color.toArgb()
                    this.textSize = fontSize.toPx()
                },
            )
        }
    }
}