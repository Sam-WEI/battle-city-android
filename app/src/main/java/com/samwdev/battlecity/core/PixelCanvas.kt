package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.samwdev.battlecity.ui.components.LocalMapPixelDp
import com.samwdev.battlecity.ui.components.mpx2dp
import kotlin.math.max
import kotlin.math.min

/**
 * This canvas allows you to draw in original game pixel
 */
@Composable
fun PixelCanvas(
    modifier: Modifier = Modifier,
    widthInMapPixel: MapPixel = 1f.grid2mpx,
    heightInMapPixel: MapPixel = 1f.grid2mpx,
    topLeftInMapPixel: Offset = Offset.Zero,
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
    fun drawPixelPoint(color: Color, topLeft: Offset) {
        drawRect(color = color, topLeft = topLeft, size = Size(1f, 1f))
    }

    fun drawVerticalLine(color: Color, topLeft: Offset, length: MapPixel) {
        drawRect(color = color, topLeft = topLeft, size = Size(1f, length))
    }

    fun drawHorizontalLine(color: Color, topLeft: Offset, length: MapPixel) {
        drawRect(color = color, topLeft = topLeft, size = Size(length, 1f))
    }

    fun drawDiagonalLine(color: Color, end1: Offset, end2: Offset) {
        val l = min(end1.x.toInt(), end2.x.toInt())
        val r = max(end1.x.toInt(), end2.x.toInt())
        val t = min(end1.y.toInt(), end2.y.toInt())
        val b = max(end1.y.toInt(), end2.y.toInt())

    }
}