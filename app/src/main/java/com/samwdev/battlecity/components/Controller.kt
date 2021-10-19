package com.samwdev.battlecity.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

private val joyStickBgBrush = Brush.radialGradient(colors = listOf(Color.Gray, Color.LightGray))

@Composable
fun Controller(
    modifier: Modifier = Modifier,
    onSteer: (Offset) -> Unit,
    onFire: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        JoyStick(
            modifier = Modifier.size(100.dp),
            onChange = onSteer
        )
        FireButton(
            modifier = Modifier.size(60.dp),
            onTap = onFire,
        )
    }
}

@Composable
fun JoyStick(modifier: Modifier = Modifier, onChange: (Offset) -> Unit) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    Canvas(
        modifier = modifier
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

@Composable
fun FireButton(modifier: Modifier = Modifier, onTap: () -> Unit) {
    val color by remember { mutableStateOf(Color.LightGray) }
    Canvas(modifier = modifier.pointerInput(Unit) {
        coroutineScope {
            while (true) {
                awaitPointerEventScope { awaitFirstDown() }

                onTap()
            }
        }
    }) {
        drawCircle(color)
    }
}