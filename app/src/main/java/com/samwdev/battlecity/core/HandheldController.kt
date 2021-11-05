package com.samwdev.battlecity.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

@Composable
fun rememberHandheldControllerState(): HandheldControllerState {
    return remember { HandheldControllerState() }
}

class HandheldControllerState {
    var direction by mutableStateOf(Direction.Unspecified)
        private set

    var offset by mutableStateOf(Offset.Unspecified)
        private set

    var firePressed by mutableStateOf(false)
        private set

    fun setSteerInput(offset: Offset) {
        this.offset = offset
        direction = getDirection(offset)
    }

    fun setFireInput(pressed: Boolean) {
        firePressed = pressed
    }
}

private fun getDirection(steerOffset: Offset): Direction {
    val (x, y) = steerOffset
    val angle = Math.toDegrees(atan2(y, x).toDouble())

    return when {
        x == 0f && y == 0f -> Direction.Unspecified
        angle <= -45 && angle > -135 -> Direction.Up
        angle <= -135 || angle > 135 -> Direction.Left
        angle <= 135 && angle > 45 -> Direction.Down
        angle <= 45 && angle > 0 || angle > -45 && angle <= 0 -> Direction.Right
        else -> Direction.Unspecified
    }
}

private val joyStickBgColor = listOf(Color.Gray, Color.LightGray)

@Composable
fun HandheldController(
    modifier: Modifier = Modifier,
    handheldControllerState: HandheldControllerState = rememberHandheldControllerState(),
    onSteer: (Offset) -> Unit = { handheldControllerState.setSteerInput(it) },
    onFire: (Boolean) -> Unit = { handheldControllerState.setFireInput(it) },
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
            onPress = onFire,
        )
    }
}

@Composable
fun JoyStick(
    modifier: Modifier = Modifier,
    idleRadiusFraction: Float = 0.15f,
    onChange: (Offset) -> Unit,
) {
    val joystickPosition = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    // the BoxWithConstraints provides maxWidth/maxHeight for calculating the joystick's center.
    // PointerInputScope itself provides a size field, but it's always (0,0) after activity restarts possibly due to a bug.
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        Canvas(
            modifier = modifier
                .pointerInput(Unit) {
                    val center = Offset(maxWidth.toPx() / 2f, maxHeight.toPx() / 2f)
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
            if (Offset(x, y).getDistanceSquared() < (idleRadiusFraction * side).pow(2)) {
                onChange(Offset(0f, 0f))
            } else {
                onChange(Offset(x / allowedRadius, y / allowedRadius))
            }

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

}

@Composable
fun FireButton(modifier: Modifier = Modifier, onPress: (Boolean) -> Unit) {
    var pressing by remember { mutableStateOf(false) }
    val color by animateColorAsState(targetValue = if (pressing) Color.DarkGray else Color.Gray)

    Canvas(modifier = modifier.pointerInput(Unit) {
        coroutineScope {
            while (true) {
                awaitPointerEventScope { awaitFirstDown() }
                pressing = true
                onPress(true)
                awaitPointerEventScope { waitForUpOrCancellation() }
                pressing = false
                onPress(false)
            }
        }
    }) {
        drawCircle(color)
    }
}