package com.samwdev.battlecity.ui.component

import android.content.res.Resources
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.samwdev.battlecity.R
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.cell2mpx
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
    widthInMapPixel: MapPixel = 1f.cell2mpx,
    heightInMapPixel: MapPixel = 1f.cell2mpx,
    onDraw: PixelDrawScope.() -> Unit
) {
    val mpxDp = LocalMapPixelDp.current
    Canvas(
        modifier = modifier
            .size(widthInMapPixel.mpx2dp, heightInMapPixel.mpx2dp)
            .offset(topLeftInMapPixel.x.mpx2dp, topLeftInMapPixel.y.mpx2dp)
    ) {
        scale(mpxDp.toPx(), pivot = Offset.Zero) {
            PixelDrawScope(this).onDraw()
        }
    }
}

val LocalPixelFontPaint = staticCompositionLocalOf<NativePaint> {
    error("Pixel font paint not provided.")
}

// todo move to better place. Revisit the impl.
@Composable
fun PixelTextPaintScope(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val tf = try {
        ResourcesCompat.getFont(context, R.font.ps2p_font)
    } catch (e: Resources.NotFoundException) {
        Typeface.DEFAULT
    }
    val textPaint = remember {
        Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.BLACK
            textSize = 8f
            typeface = tf
        }
    }
    CompositionLocalProvider(LocalPixelFontPaint provides textPaint) {
        content()
    }
}

class PixelDrawScope(private val drawScope: DrawScope) : DrawScope by drawScope {
    private val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 12.sp.toPx()
        color = android.graphics.Color.BLUE
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    fun drawPixel(
        color: Color,
        topLeft: Offset,
        blendMode: BlendMode = DrawScope.DefaultBlendMode
    ) {
        drawRect(color = color, topLeft = topLeft, size = Size(1f, 1f), blendMode = blendMode)
    }

    fun drawVerticalLine(
        color: Color,
        topLeft: Offset,
        length: MapPixel,
        blendMode: BlendMode = DrawScope.DefaultBlendMode
    ) {
        drawRect(color = color, topLeft = topLeft, size = Size(1f, length), blendMode = blendMode)
    }

    fun drawHorizontalLine(
        color: Color,
        topLeft: Offset,
        length: MapPixel,
        blendMode: BlendMode = DrawScope.DefaultBlendMode
    ) {
        drawRect(color = color, topLeft = topLeft, size = Size(length, 1f), blendMode = blendMode)
    }

    fun drawSquare(
        color: Color,
        topLeft: Offset,
        side: MapPixel,
        blendMode: BlendMode = DrawScope.DefaultBlendMode
    ) {
        drawRect(color = color, topLeft = topLeft, size = Size(side, side), blendMode = blendMode)
    }

    fun drawDiagonalLine(
        color: Color,
        end1: Offset,
        end2: Offset,
        blendMode: BlendMode = DrawScope.DefaultBlendMode
    ) {
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
                drawPixel(color = color, topLeft = Offset(x.toFloat(), y), blendMode = blendMode)
            }
        } else {
            val k = (end1.x - end2.x) / (end1.y - end2.y)
            val b = end2.x - k * end2.y
            for (y in top.toInt()..bottom.toInt()) {
                val x = (y * k + b).roundToInt().toFloat()
                drawPixel(color = color, topLeft = Offset(x, y.toFloat()), blendMode = blendMode)
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

    fun drawPixelText(text: String, topLeft: Offset, paint: NativePaint) {
        drawIntoCanvas {
            it.nativeCanvas.drawText(text, topLeft.x, topLeft.y + 8f, paint)
        }
    }
}

inline fun DrawScope.drawForDirection(
    direction: Direction,
    pivot: Offset = Offset.Zero,
    block: DrawScope.() -> Unit
) = withTransform({
    when (direction) {
        Direction.Up -> {
            // do nothing
        }
        Direction.Down -> {
            scale(1f, -1f, pivot = pivot)
        }
        Direction.Left -> {
            scale(1f, -1f, pivot = pivot)
            rotate(direction.degreeF, pivot)
        }
        Direction.Right -> {
            rotate(direction.degreeF, pivot)
        }
    }
}, block)
