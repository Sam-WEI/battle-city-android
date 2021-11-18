package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.TANK_MAP_PIXEL
import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.roundToInt


private const val TreadPatternCycleDistance = 10

@Composable
fun Tank(tank: Tank) {
    val color1 = Color(227, 227, 137)
    val color2 = Color(227, 145, 30)
    val color3 = Color(96, 96, 0)

    var travelDistance by remember { mutableStateOf(0) }
    val treadPattern: Int by remember {
        derivedStateOf {
            // calculate tread pattern based on travel distance, so faster tank looks faster
            if (travelDistance % (TreadPatternCycleDistance * 2) < TreadPatternCycleDistance) 0 else 1
        }
    }

    if (tank.isMoving) {
        val tick = LocalTick.current
        travelDistance = remember(tick) {
            travelDistance + (tank.speed * tick.delta).roundToInt()
        }
    }

    PixelCanvas(
        heightInMapPixel = TANK_MAP_PIXEL,
        widthInMapPixel = TANK_MAP_PIXEL,
        topLeftInMapPixel = Offset(tank.x, tank.y) ,
        modifier = Modifier
            .rotate(tank.direction.degree)
    ) {
        // left tread
        translate(1f, 4f) {
            drawRect(
                color = color1,
                topLeft = Offset(0f, 0f),
                size = Size(3f, 11f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(1f, 0f),
                size = Size(2f, 11f),
            )
            drawRect(
                color = color1,
                topLeft = Offset(2f, 1f),
                size = Size(1f, 9f),
            )
            for (i in treadPattern..10 step 2) {
                val left = if (i == 0 || i == 10) 1 else 0
                drawRect(
                    color = color3,
                    topLeft = Offset(left.toFloat(), i.toFloat()),
                    size = Size(2f, 1f),
                )
            }
        }

        // right tread
        translate(left = 11f, top = 4f) {
            this as PixelDrawScope
            drawPixel(color = color1, topLeft = Offset(0f, 0f))
            drawRect(
                color = color3,
                topLeft = Offset(0f, 1f),
                size = Size(1f, 10f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(1f, 0f),
                size = Size(2f, 11f),
            )
            for (i in treadPattern..10 step 2) {
                drawRect(
                    color = color3,
                    topLeft = Offset(1f, i.toFloat()),
                    size = Size(2f, 1f),
                )
            }
        }

        translate(4f, 6f) {
            this as PixelDrawScope
            // body
            drawRect(
                color = color1,
                topLeft = Offset(0f, 1f),
                size = Size(7f, 6f),
            )
            drawRect(
                color = color1,
                topLeft = Offset(1f, 0f),
                size = Size(5f, 8f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(2f, 0f),
                size = Size(4f, 2f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(4f, 2f),
                size = Size(3f, 4f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(3f, 3f),
                size = Size(3f, 4f),
            )
            drawRect(
                color = color2,
                topLeft = Offset(1f, 2f),
                size = Size(1f, 3f),
            )
            drawPixel(color = color2, topLeft = Offset(2f, 5f))

            // body shadow
            drawRect(
                color = color3,
                topLeft = Offset(4f, 0f),
                size = Size(2f, 1f),
            )
            drawPixel(color = color3, topLeft = Offset(6f, 1f))
            drawRect(
                color = color3,
                topLeft = Offset(4f, 3f),
                size = Size(1f, 3f),
            )
            drawPixel(color = color3, topLeft = Offset(3f, 5f))
            drawPixel(color = color3, topLeft = Offset(0f, 6f))
            drawPixel(color = color3, topLeft = Offset(6f, 6f))
            drawRect(
                color = color3,
                topLeft = Offset(1f, 7f),
                size = Size(5f, 1f),
            )
        }

        // barrel
        drawVerticalLine(
            color = color1,
            topLeft = Offset(7f, 2f),
            length = 5f,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(1000.dp)) {
            Tank(
                Tank(
                id = 0,
            )
            )
        }
    }
}