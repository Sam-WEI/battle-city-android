package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.core.TankLevel
import com.samwdev.battlecity.core.TankSide
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

private val BotTankWhite = Color.White
private val BotTankGray = Color(163, 163, 163)
private val BotTankBlue = Color(0, 58, 65)

fun PixelDrawScope.drawBotTankLevel1(treadPattern: Int) {
    // barrel
    drawVerticalLine(color = BotTankWhite, topLeft = Offset(7f, 0f), length = 6f)
    // left tread
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
    // right tread
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
    // body
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

fun PixelDrawScope.drawBotTankLevel2(treadPattern: Int) {
    // barrel
    drawVerticalLine(color = BotTankWhite, topLeft = Offset(7f, 0f), length = 6f)
    // treads
    listOf(1f, 12f).forEach { tx ->
        translate(tx, 2f) {
            this as PixelDrawScope
            for (y in listOf(0f, 5f, 10f)) {
                drawRect(color = BotTankBlue, topLeft = Offset(0f, y.toFloat()), size = Size(2f, 3f))
                drawPixel(color = BotTankGray, topLeft = Offset(0f, y + treadPattern.toFloat()))
            }
        }
    }
    // body
    translate(3f, 2f) {
         this as PixelDrawScope
        drawPixel(color = BotTankWhite, topLeft = Offset(0f, 1f,))
        drawVerticalLine(color = BotTankGray, topLeft = Offset(0f, 2f), 10f)

        drawVerticalLine(color = BotTankWhite, topLeft = Offset(1f, 0f), 10f)
        drawPixel(color = BotTankGray, topLeft = Offset(1f, 3f))

        drawHorizontalLine(color = BotTankGray, topLeft = Offset(1f, 10f), 2f)
        drawRect(color = BotTankBlue, topLeft = Offset(1f, 11f), size = Size(7f, 2f))

        drawVerticalLine(color = BotTankWhite, topLeft = Offset(2f, 0f), 10f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(2f, 4f), 5f)

        drawVerticalLine(color = BotTankBlue, topLeft = Offset(3f, 0f), 3f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(3f, 3f), 7f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(3f, 6f), 2f)
        drawHorizontalLine(color = BotTankWhite, topLeft = Offset(3f, 10f), 3f)

        drawVerticalLine(color = BotTankGray, topLeft = Offset(4f, 4f), 6f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(4f, 5f), 2f)
        drawPixel(color = BotTankWhite, topLeft = Offset(4f, 7f))
        drawPixel(color = BotTankWhite, topLeft = Offset(4f, 12f))

        drawVerticalLine(color = BotTankBlue, topLeft = Offset(5f, 0f), 3f)
        drawVerticalLine(color = BotTankGray, topLeft = Offset(5f, 3f), 7f)
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(5f, 6f), 2f)

        drawVerticalLine(color = BotTankGray, topLeft = Offset(6f, 0f), 9f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(6f, 2f), 2f)
        drawPixel(color = BotTankWhite, topLeft = Offset(6f, 9f))
        drawPixel(color = BotTankBlue, topLeft = Offset(6f, 10f))

        drawVerticalLine(color = BotTankGray, topLeft = Offset(7f, 0f), 2f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(7f, 2f), 10f)

        drawVerticalLine(color = BotTankGray, topLeft = Offset(8f, 1f), 11f)
    }
}

fun PixelDrawScope.drawBotTankLevel3(treadPattern: Int) {
    // level 3 looks similar to level 1, do changes above it
    drawBotTankLevel1(treadPattern)
    // barrel head
    drawHorizontalLine(color = BotTankWhite, topLeft = Offset(6f, 0f), length = 2f)
    drawPixel(color = BotTankGray, topLeft = Offset(8f, 0f))
    translate(4f, 3f) {
        this as PixelDrawScope
        drawPixel(color = BotTankWhite, topLeft = Offset(1f, 10f))
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(2f, 9f), 2f)
        drawPixel(color = BotTankGray, topLeft = Offset(2f, 11f))
        drawPixel(color = BotTankWhite, topLeft = Offset(3f, 11f))

        drawPixel(color = BotTankBlue, topLeft = Offset(4f, 11f))
        drawPixel(color = BotTankBlue, topLeft = Offset(5f, 10f))
    }
    // left tread update
    translate(1f, 3f) {
        this as PixelDrawScope
        drawPixel(color = BotTankWhite, topLeft = Offset(0f, 11f))
        drawHorizontalLine(color = BotTankGray, topLeft = Offset(1f, 11f), length = 2f)
        if (treadPattern == 0) {
            drawHorizontalLine(color = BotTankBlue, topLeft = Offset(0f, 11f), length = 2f)
        }
    }
    // right tread update
    translate(11f, 3f) {
        this as PixelDrawScope
        drawHorizontalLine(color = BotTankGray, topLeft = Offset(0f, 11f), length = 3f)
        if (treadPattern == 0) {
            drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, 11f), length = 2f)
        }
    }
}

fun PixelDrawScope.drawBotTankLevel4(treadPattern: Int) {
    translate(1f, 0f) {
        this as PixelDrawScope
        drawRect(color = BotTankGray, topLeft = Offset(0f, 0f), size = Size(3f, 15f))
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(0f, 0f), length = 15f)
        for (y in treadPattern..14 step 2) {
            drawHorizontalLine(color = BotTankBlue, topLeft = Offset(0f, y.toFloat()), length = 2f)
        }
        if (treadPattern == 0) {
            drawPixel(color = BotTankGray, topLeft = Offset(0f, 0f))
        }
    }
    translate(11f, 0f) {
        this as PixelDrawScope
        drawRect(color = BotTankGray, topLeft = Offset(0f, 0f), size = Size(3f, 15f))
        drawPixel(color = BotTankWhite, topLeft = Offset(0f, 0f))
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(0f, 3f), length = 10f)
        drawPixel(color = BotTankBlue, topLeft = Offset(0f, 14f))
        for (y in treadPattern..14 step 2) {
            drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, y.toFloat()), length = 2f)
        }
    }
    translate(4f, 1f) {
        this as PixelDrawScope
        drawRect(color = BotTankGray, topLeft = Offset(0f, 0f), size = Size(7f, 12f))
        drawHorizontalLine(color = BotTankBlue, topLeft = Offset(0f, 12f), length = 7f)
        drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, 11f), length = 5f)
        drawHorizontalLine(color = BotTankBlue, topLeft = Offset(1f, 0f), length = 5f)
        drawPixel(color = BotTankGray, topLeft = Offset(3f, 13f))

        drawVerticalLine(color = BotTankWhite, topLeft = Offset(0f, 2f), length = 9f)
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(1f, 4f), length = 2f)
        drawHorizontalLine(color = BotTankWhite, topLeft = Offset(1f, 5f), length = 5f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(4f, 1f), length = 4f)
        drawVerticalLine(color = BotTankWhite, topLeft = Offset(5f, 3f), length = 3f)

        drawRect(color = BotTankWhite, topLeft = Offset(3f, 7f), size = Size(2f, 2f))
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(2f, 7f), length = 2f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(3f, 6f), length = 2f)
        drawVerticalLine(color = BotTankBlue, topLeft = Offset(6f, 4f), length = 7f)
    }
    // barrel
    drawVerticalLine(color = BotTankWhite, topLeft = Offset(6f, 0f), length = 2f)
    drawVerticalLine(color = BotTankWhite, topLeft = Offset(7f, 0f), length = 6f)
    drawVerticalLine(color = BotTankGray, topLeft = Offset(8f, 0f), length = 2f)

}

@Preview(showBackground = true)
@Composable
private fun BotTankPreview() {
    BattleCityTheme {
        Map(modifier = Modifier
            .size(500.dp)
            .background(Color.DarkGray), sideBlockCount = 4
        ) {
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 0f.grid2mpx,
                y = 0f.grid2mpx,
                level = TankLevel.Level1,
                side = TankSide.Bot,
                hp = 1)
            )
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 0f.grid2mpx,
                y = 1f.grid2mpx,
                level = TankLevel.Level2,
                side = TankSide.Bot,
                hp = 1)
            )
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 0f.grid2mpx,
                y = 2f.grid2mpx,
                level = TankLevel.Level3,
                side = TankSide.Bot,
                hp = 1)
            )
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 0f.grid2mpx,
                y = 3f.grid2mpx,
                level = TankLevel.Level4,
                side = TankSide.Bot,
                hp = 1)
            )
        }
    }
}