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

fun PixelDrawScope.drawBotTankLevel1(treadPattern: Int, palette: TankColorPalette) {
    // barrel
    drawVerticalLine(color = palette.light, topLeft = Offset(7f, 0f), length = 6f)
    // left tread
    translate(1f, 3f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 11f))
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 11f)
        if (treadPattern == 0) {
            for (y in 1..10 step 2) {
                drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, y.toFloat()), length = 2f)
            }
        } else {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 0f), length = 2f)
            for (y in 2..10 step 2) {
                drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, y.toFloat()), length = 2f)
            }
        }
    }
    // right tread
    translate(11f, 3f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 11f))
        drawPixel(color = palette.light, topLeft = Offset(0f, 0f))
        if (treadPattern == 0) {
            for (y in 1..10 step 2) {
                drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, y.toFloat()), length = 2f)
            }
        } else {
            for (y in 0..10 step 2) {
                drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, y.toFloat()), length = 2f)
            }
        }
    }
    // body
    translate(4f, 3f) {
        this as PixelDrawScope
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 2f), length = 7f)
        drawVerticalLine(color = palette.light, topLeft = Offset(1f, 1f), length = 9f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(1f, 4f), length = 4f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(2f, 0f), length = 10f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(2f, 5f), length = 2f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(3f, 3f), length = 7f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(3f, 4f), length = 2f)
        drawPixel(color = palette.light, topLeft = Offset(3f, 6f))

        drawVerticalLine(color = palette.dark, topLeft = Offset(4f, 0f), length = 10f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(4f, 3f), length = 6f)
        drawVerticalLine(color = palette.light, topLeft = Offset(4f, 5f), length = 2f)

        drawVerticalLine(color = palette.dark, topLeft = Offset(5f, 1f), length = 9f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(5f, 4f), length = 4f)

        drawVerticalLine(color = palette.dark, topLeft = Offset(6f, 2f), length = 7f)

        drawHorizontalLine(color = palette.dark, topLeft = Offset(2f, 10f), length = 3f)
        drawPixel(color = palette.medium, topLeft = Offset(3f, 11f))
    }
}

fun PixelDrawScope.drawBotTankLevel2(treadPattern: Int, palette: TankColorPalette) {
    // barrel
    drawVerticalLine(color = palette.light, topLeft = Offset(7f, 0f), length = 6f)
    // treads
    listOf(1f, 12f).forEach { tx ->
        translate(tx, 2f) {
            this as PixelDrawScope
            for (y in listOf(0f, 5f, 10f)) {
                drawRect(color = palette.dark, topLeft = Offset(0f, y.toFloat()), size = Size(2f, 3f))
                drawPixel(color = palette.medium, topLeft = Offset(0f, y + treadPattern.toFloat()))
            }
        }
    }
    // body
    translate(3f, 2f) {
         this as PixelDrawScope
        drawPixel(color = palette.light, topLeft = Offset(0f, 1f,))
        drawVerticalLine(color = palette.medium, topLeft = Offset(0f, 2f), 10f)

        drawVerticalLine(color = palette.light, topLeft = Offset(1f, 0f), 10f)
        drawPixel(color = palette.medium, topLeft = Offset(1f, 3f))

        drawHorizontalLine(color = palette.medium, topLeft = Offset(1f, 10f), 2f)
        drawRect(color = palette.dark, topLeft = Offset(1f, 11f), size = Size(7f, 2f))

        drawVerticalLine(color = palette.light, topLeft = Offset(2f, 0f), 10f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(2f, 4f), 5f)

        drawVerticalLine(color = palette.dark, topLeft = Offset(3f, 0f), 3f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(3f, 3f), 7f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(3f, 6f), 2f)
        drawHorizontalLine(color = palette.light, topLeft = Offset(3f, 10f), 3f)

        drawVerticalLine(color = palette.medium, topLeft = Offset(4f, 4f), 6f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(4f, 5f), 2f)
        drawPixel(color = palette.light, topLeft = Offset(4f, 7f))
        drawPixel(color = palette.light, topLeft = Offset(4f, 12f))

        drawVerticalLine(color = palette.dark, topLeft = Offset(5f, 0f), 3f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(5f, 3f), 7f)
        drawVerticalLine(color = palette.light, topLeft = Offset(5f, 6f), 2f)

        drawVerticalLine(color = palette.medium, topLeft = Offset(6f, 0f), 9f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(6f, 2f), 2f)
        drawPixel(color = palette.light, topLeft = Offset(6f, 9f))
        drawPixel(color = palette.dark, topLeft = Offset(6f, 10f))

        drawVerticalLine(color = palette.medium, topLeft = Offset(7f, 0f), 2f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(7f, 2f), 10f)

        drawVerticalLine(color = palette.medium, topLeft = Offset(8f, 1f), 11f)
    }
}

fun PixelDrawScope.drawBotTankLevel3(treadPattern: Int, palette: TankColorPalette) {
    // level 3 looks similar to level 1, do changes above it
    drawBotTankLevel1(treadPattern, palette)
    // barrel head
    drawHorizontalLine(color = palette.light, topLeft = Offset(6f, 0f), length = 2f)
    drawPixel(color = palette.medium, topLeft = Offset(8f, 0f))
    translate(4f, 3f) {
        this as PixelDrawScope
        drawPixel(color = palette.light, topLeft = Offset(1f, 10f))
        drawVerticalLine(color = palette.light, topLeft = Offset(2f, 9f), 2f)
        drawPixel(color = palette.medium, topLeft = Offset(2f, 11f))
        drawPixel(color = palette.light, topLeft = Offset(3f, 11f))

        drawPixel(color = palette.dark, topLeft = Offset(4f, 11f))
        drawPixel(color = palette.dark, topLeft = Offset(5f, 10f))
    }
    // left tread update
    translate(1f, 3f) {
        this as PixelDrawScope
        drawPixel(color = palette.light, topLeft = Offset(0f, 11f))
        drawHorizontalLine(color = palette.medium, topLeft = Offset(1f, 11f), length = 2f)
        if (treadPattern == 0) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, 11f), length = 2f)
        }
    }
    // right tread update
    translate(11f, 3f) {
        this as PixelDrawScope
        drawHorizontalLine(color = palette.medium, topLeft = Offset(0f, 11f), length = 3f)
        if (treadPattern == 0) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 11f), length = 2f)
        }
    }
}

fun PixelDrawScope.drawBotTankLevel4(treadPattern: Int, palette: TankColorPalette) {
    translate(1f, 0f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 15f))
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 15f)
        for (y in treadPattern..14 step 2) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, y.toFloat()), length = 2f)
        }
        if (treadPattern == 0) {
            drawPixel(color = palette.medium, topLeft = Offset(0f, 0f))
        }
    }
    translate(11f, 0f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 15f))
        drawPixel(color = palette.light, topLeft = Offset(0f, 0f))
        drawVerticalLine(color = palette.dark, topLeft = Offset(0f, 3f), length = 10f)
        drawPixel(color = palette.dark, topLeft = Offset(0f, 14f))
        for (y in treadPattern..14 step 2) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, y.toFloat()), length = 2f)
        }
    }
    translate(4f, 1f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(7f, 12f))
        drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, 12f), length = 7f)
        drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 11f), length = 5f)
        drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 0f), length = 5f)
        drawPixel(color = palette.medium, topLeft = Offset(3f, 13f))

        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 2f), length = 9f)
        drawVerticalLine(color = palette.light, topLeft = Offset(1f, 4f), length = 2f)
        drawHorizontalLine(color = palette.light, topLeft = Offset(1f, 5f), length = 5f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(4f, 1f), length = 4f)
        drawVerticalLine(color = palette.light, topLeft = Offset(5f, 3f), length = 3f)

        drawRect(color = palette.light, topLeft = Offset(3f, 7f), size = Size(2f, 2f))
        drawVerticalLine(color = palette.dark, topLeft = Offset(2f, 7f), length = 2f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(3f, 6f), length = 2f)
        drawVerticalLine(color = palette.dark, topLeft = Offset(6f, 4f), length = 7f)
    }
    // barrel
    drawVerticalLine(color = palette.light, topLeft = Offset(6f, 0f), length = 2f)
    drawVerticalLine(color = palette.light, topLeft = Offset(7f, 0f), length = 6f)
    drawVerticalLine(color = palette.medium, topLeft = Offset(8f, 0f), length = 2f)

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
                x = 2f.grid2mpx,
                y = 0f.grid2mpx,
                level = TankLevel.Level4,
                side = TankSide.Bot,
                hp = 1)
            )
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 2f.grid2mpx,
                y = 1f.grid2mpx,
                level = TankLevel.Level4,
                side = TankSide.Bot,
                hp = 1),
                palette = BotLevel4GreenPalette
            )
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 2f.grid2mpx,
                y = 2f.grid2mpx,
                level = TankLevel.Level4,
                side = TankSide.Bot,
                hp = 1),
                palette = BotLevel4YellowPalette
            )
            TankTreadsPreview(tank = Tank(
                id = 0,
                x = 2f.grid2mpx,
                y = 3f.grid2mpx,
                level = TankLevel.Level4,
                side = TankSide.Bot,
                hp = 1),
                palette = BotPowerUpPalette
            )
        }
    }
}