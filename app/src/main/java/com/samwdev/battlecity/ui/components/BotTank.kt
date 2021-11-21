package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val BotTankWhite = Color.White
private val BotTankGray = Color(163, 163, 163)
private val BotTankBlue = Color(0, 58, 65)

@Composable
fun BotTank(tank: Tank) {
    PixelCanvas(
        heightInMapPixel = tank.pivotBox.height.grid2mpx,
        widthInMapPixel = tank.pivotBox.width.grid2mpx,
        topLeftInMapPixel = Offset(tank.pivotBox.left, tank.pivotBox.top),
        modifier = Modifier.clipToBounds()
    ) {
        drawBotTank(1)
    }
}

fun PixelDrawScope.drawBotTank(treadPattern: Int) {
    drawVerticalLine(color = BotTankWhite, topLeft = Offset(7f, 0f), length = 6f)
    translate(1f, 3f) {
        this as PixelDrawScope
        drawRect(color = BotTankGray, topLeft = Offset(0f, 0f), size = Size(3f, 11f))
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(0f, 0f), length = 11f)
        if (treadPattern == 0) {
            for (y in 1..10 step 2) {
                drawHorizontalLine(color = BotTankBlue, topLeft = Offset(0f, y.toFloat()), length = 2f)
            }
        } else {
            drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, 0f), length = 2f)
            for (y in 2..10 step 2) {
                drawHorizontalLine(color = BotTankBlue, topLeft = Offset(0f, y.toFloat()), length = 2f)
            }
        }
    }

    translate(11f, 3f) {
        this as PixelDrawScope
        drawRect(color = BotTankGray, topLeft = Offset(0f, 0f), size = Size(3f, 11f))
        drawPixel(color = BotTankWhite, topLeft = Offset(0f, 0f))
        if (treadPattern == 0) {
            for (y in 1..10 step 2) {
                drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, y.toFloat()), length = 2f)
            }
        } else {
            for (y in 0..10 step 2) {
                drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, y.toFloat()), length = 2f)
            }
        }

    }

    translate(4f, 3f) {
        this as PixelDrawScope
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(0f, 2f), length = 7f)
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(1f, 1f), length = 9f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(1f, 4f), length = 4f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(2f, 0f), length = 10f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(2f, 5f), length = 2f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(3f, 3f), length = 7f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(3f, 4f), length = 2f)
        drawPixel(color = BotTankWhite, topLeft = Offset(3f, 6f))

        drawVerticalLine(color = BotTankBlue, topLeft = Offset(4f, 0f), length = 10f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(4f, 3f), length = 6f)
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(4f, 5f), length = 2f)

        drawVerticalLine(color = BotTankBlue, topLeft = Offset(5f, 1f), length = 9f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(5f, 4f), length = 4f)

        drawVerticalLine(color = BotTankBlue, topLeft = Offset(6f, 2f), length = 7f)

        drawHorizontalLine(color = BotTankBlue, topLeft = Offset(2f, 10f), length = 3f)
        drawPixel(color = BotTankGray, topLeft = Offset(3f, 11f))
    }
}

@Preview(showBackground = true)
@Composable
private fun BotTankPreview() {
    BattleCityTheme {
        CompositionLocalProvider(LocalDebugConfig provides DebugConfig(showPivotBox = true)) {
            Map(modifier = Modifier.size(500.dp).background(Color.DarkGray), sideBlockCount = 4) {
                BotTank(Tank(id = 0, side = TankSide.Bot, hp = 1))
            }
        }
    }
}