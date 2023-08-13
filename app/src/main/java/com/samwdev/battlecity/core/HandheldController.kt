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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.utils.VibratorHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.*

class HandheldControllerState {
    private var directionsInLastTick = LinkedHashSet<Direction>()

    private var firePressedInLastTick = LinkedHashSet<Boolean>()

    fun setSteerInput(direction: Direction?) {
        direction?.let { dir ->
            // remove and add again to bring the latest input to the end
            directionsInLastTick.remove(dir)
            directionsInLastTick.add(dir)
        }
    }

    fun setFireInput(pressed: Boolean) {
        firePressedInLastTick.remove(pressed)
        firePressedInLastTick.add(pressed)
    }

    fun consumeSteerInput(): List<Direction> {
        return directionsInLastTick.toList().also {
            directionsInLastTick.clear()
        }
    }

    fun consumeFire(): Boolean {
        val firedInLastTick = firePressedInLastTick.contains(true)
        if (firePressedInLastTick.lastOrNull() == false) {
            // player released the fire button in last tick
            firePressedInLastTick.clear()
        }
        return firedInLastTick
    }
}

private val joyStickBgColor = listOf(Color.Gray, Color.LightGray)

@Composable
fun HandheldController(
    modifier: Modifier = Modifier,
    onSteer: (Direction?) -> Unit,
    onFire: (Boolean) -> Unit,
) {
    val context = LocalContext.current.applicationContext
    val coroutine = rememberCoroutineScope()
    val vibrator = remember(context) { VibratorHelper(context) }
    var lastDirection: Direction? by remember { mutableStateOf(null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        JoyStick(
            diameter = 100.dp,
            onChange = {
                onSteer(it)
                if (it != null && lastDirection != it) {
                    coroutine.launch {
                        // may not even need a coroutine
                        vibrator.vibrateFire()
                    }
                }
                lastDirection = it
            }
        )
        FireButton(
            modifier = Modifier.size(60.dp),
            onPress = { fire ->
                onFire(fire)
                if (fire) {
                    coroutine.launch {
                        vibrator.vibrateFire()
                    }
                }
            },
        )
    }
}

@Composable
fun JoyStick(
    diameter: Dp,
    modifier: Modifier = Modifier,
    idleRadiusFraction: Float = 0.15f,
    onChange: (Direction?) -> Unit,
) {
    val density = LocalDensity.current.density
    val center = Offset(diameter.value * density / 2, diameter.value * density / 2)
    val joystickPositionAnim = remember { Animatable(center, Offset.VectorConverter) }
    Canvas(
        modifier = modifier
            .size(diameter)
            .aspectRatio(1f)
            .pointerInput(Unit) {
                joystickPositionAnim.snapTo(center)
                coroutineScope {
                    while (true) {
                        val pointer = awaitPointerEventScope { awaitFirstDown() }
                        val pointerId = pointer.id
                        joystickPositionAnim.snapTo(pointer.position)
                        awaitPointerEventScope {
                            drag(pointerId = pointerId) {
                                launch {
                                    joystickPositionAnim.snapTo(it.position)
                                }
                            }
                        }
                        launch {
                            joystickPositionAnim.animateTo(center, animationSpec = spring())
                        }
                    }
                }
            }
    ) {
        val side = min(size.width, size.height)
        val allowedRadius = 0.9f * side / 2
        val (x, y) = joystickPositionAnim.value - center
        val drawPosition: Offset = if (x.pow(2) + y.pow(2) < allowedRadius.pow(2)) {
            joystickPositionAnim.value
        } else {
            val angle = atan2(x, y)
            Offset(sin(angle) * allowedRadius, cos(angle) * allowedRadius) + center
        }
        if (Offset(x, y).getDistanceSquared() < (idleRadiusFraction * side).pow(2)) {
            onChange(Offset(0f, 0f).steerDirection)
        } else {
            onChange(Offset(x / allowedRadius, y / allowedRadius).steerDirection)
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

private val Offset.steerDirection: Direction? get() {
    val (x, y) = this
    val angle = Math.toDegrees(atan2(y, x).toDouble())
    return when {
        x == 0f && y == 0f -> null
        angle <= -45 && angle > -135 -> Direction.Up
        angle <= -135 || angle > 135 -> Direction.Left
        angle <= 135 && angle > 45 -> Direction.Down
        angle <= 45 && angle > 0 || angle > -45 && angle <= 0 -> Direction.Right
        else -> null
    }
}

@Composable
fun FireButton(modifier: Modifier = Modifier, onPress: (Boolean) -> Unit) {
    var pressing by remember { mutableStateOf(false) }
    val color by animateColorAsState(
        targetValue = if (pressing) Color.DarkGray else Color.Gray,
        label = "fire button",
    )

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