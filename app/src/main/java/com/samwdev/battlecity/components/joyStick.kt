package com.samwdev.battlecity.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

private val joyStickBg = Color.Gray
private val joyStickBgBrush = Brush.radialGradient(colors = listOf(Color.Gray, Color.LightGray))

@Composable
fun JoyStick(modifier: Modifier = Modifier, onChange: (Offset) -> Unit) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    Canvas(
        modifier = Modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                val center = Offset(size.width / 2f, size.height / 2f)
                offset.snapTo(center)
                coroutineScope {
                    while (true) {
                        val pointer = awaitPointerEventScope { awaitFirstDown() }
                        val pointerId = pointer.id
                        offset.snapTo(pointer.position)
                        awaitPointerEventScope {
                            drag(pointerId = pointerId) {
                                launch {
                                    offset.snapTo(it.position)
                                }
                            }
                        }
                        launch {
                            offset.animateTo(center, animationSpec = spring())
                        }
                    }
                }
            }
    ) {
        val side = min(size.width, size.height)
        drawCircle(
            brush = joyStickBgBrush,
            center = Offset(side / 2f, side / 2f),
            radius = side / 2f
        )
        val allowedRadius = 0.9f * side / 2
        val (x, y) = offset.value - center
        val drawPosition: Offset = if (x.pow(2) + y.pow(2) < allowedRadius.pow(2)) {
            offset.value
        } else {
            val angle = atan2(x, y)
            Offset(sin(angle) * allowedRadius, cos(angle) * allowedRadius) + center
        }
        onChange(Offset(x / allowedRadius, y / allowedRadius))
        drawCircle(
            color = Color.DarkGray,
            center = drawPosition,
            radius = side / 6f,
        )
    }
}

private fun Offset.canvasPosition(side: Float): Offset {
    val mid = side / 2f
    return Offset(x - mid, y - mid)
}

private fun Offset.calcPositionPercentage(side: Float): Offset {
    val mid = side / 2f
    return Offset((x - mid) / mid, (y - mid) / mid)
}