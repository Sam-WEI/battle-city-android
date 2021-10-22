package com.samwdev.battlecity.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
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

private val joyStickBgColor = listOf(Color.Gray, Color.LightGray)

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
    val joystickPosition = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                val center = Offset(size.width / 2f, size.height / 2f)
                joystickPosition.snapTo(center)
                coroutineScope {
                    while (true) {
                        val pointer = awaitPointerEventScope { awaitFirstDown() }
                        val pointerId = pointer.id
                        joystickPosition.snapTo(pointer.position)
                        awaitPointerEventScope {
                            drag(pointerId = pointerId) {
                                launch {
                                    joystickPosition.snapTo(it.position)
                                }
                            }
                        }
                        launch {
                            joystickPosition.animateTo(center, animationSpec = spring())
                        }
                    }
                }
            }
    ) {
        val side = min(size.width, size.height)
        val allowedRadius = 0.9f * side / 2
        val (x, y) = joystickPosition.value - center
        val drawPosition: Offset = if (x.pow(2) + y.pow(2) < allowedRadius.pow(2)) {
            joystickPosition.value
        } else {
            val angle = atan2(x, y)
            Offset(sin(angle) * allowedRadius, cos(angle) * allowedRadius) + center
        }
        onChange(Offset(x / allowedRadius, y / allowedRadius))

        drawCircle(
            brush = Brush.radialGradient(colors = joyStickBgColor, center = drawPosition, radius = 0.8f * side / 2),
            center = Offset(side / 2f, side / 2f),
            radius = side / 2f
        )
        drawCircle(
            color = Color.DarkGray,
            center = drawPosition,
            radius = side / 6f,
        )
    }
}

@Composable
fun FireButton(modifier: Modifier = Modifier, onTap: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val color by animateColorAsState(targetValue = if (pressed) Color.DarkGray else Color.Gray)

    Canvas(modifier = modifier.pointerInput(Unit) {
        coroutineScope {
            while (true) {
                awaitPointerEventScope { awaitFirstDown() }
                pressed = true
                onTap()
                awaitPointerEventScope { waitForUpOrCancellation() }
                pressed = false
            }
        }
    }) {
        drawCircle(color)
    }
}