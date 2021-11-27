package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.core.TankLevel
import com.samwdev.battlecity.core.TankSide
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme


fun PixelDrawScope.drawPlayerTankLevel1(treadPattern: Int, palette: TankColorPalette) {
    val (color1, color2, color3) = palette
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

fun PixelDrawScope.drawPlayerTankLevel2(treadPattern: Int, palette: TankColorPalette) {
    // left tread
    translate(left = 1f, top = 4f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 12f))
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 12f)
        drawPixel(color = palette.light, topLeft = Offset(2f, 1f))
        drawPixel(color = palette.light, topLeft = Offset(2f, 10f))
        if (treadPattern == 0) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 0f), length = 2f)
            drawPixel(color = palette.medium, topLeft = Offset(0f, 0f))
            for (y in 2..10 step 2) {
                drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, y.toFloat()), length = 2f)
            }
        } else {
            for (y in 1..10 step 2) {
                drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, y.toFloat()), length = 2f)
            }
            drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, 11f), length = 3f)
        }
    }
    // right tread
    translate(left = 11f, top = 4f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 12f))
        drawVerticalLine(color = palette.dark, topLeft = Offset(0f, 1f), length = 10f)
        for (y in treadPattern..11 step 2) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, y.toFloat()), length = 3f)
        }
        drawPixel(color = palette.light, topLeft = Offset(0f, 0f))
    }
    // body
    translate(left = 4f, top = 5f) {
        this as PixelDrawScope
        drawRect(color = palette.dark, topLeft = Offset(0f, 0f), size = Size(7f, 10f))
        drawRect(color = palette.light, topLeft = Offset(0f, 0f), size = Size(2f, 9f))
        drawVerticalLine(color = palette.medium, topLeft = Offset(1f, 3f), length = 5f)

        drawVerticalLine(color = palette.medium, topLeft = Offset(1f, 3f), length = 1f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(1f, 3f), length = 1f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(1f, 3f), length = 1f)

        drawRect(color = palette.dark, topLeft = Offset(2f, -1f), size = Size(3f, 2f))
        drawRect(color = palette.medium, topLeft = Offset(2f, 1f), size = Size(3f, 2f))

        drawRect(color = palette.light, topLeft = Offset(2f, 3f), size = Size(2f, 4f))
        drawVerticalLine(color = palette.medium, topLeft = Offset(2f, 7f), length = 2f)

        drawVerticalLine(color = palette.medium, topLeft = Offset(3f, 4f), length = 3f)
        drawHorizontalLine(color = palette.medium, topLeft = Offset(2f, 8f), length = 3f)

        drawPixel(color = palette.medium, topLeft = Offset(4f, 3f))
        drawVerticalLine(color = palette.medium, topLeft = Offset(5f, 3f), length = 5f)
    }
    drawVerticalLine(color = palette.light, topLeft = Offset(7f, 0f), length = 7f)
}

fun PixelDrawScope.drawPlayerTankLevel3(treadPattern: Int, palette: TankColorPalette) {
    // left tread
    translate(left = 1f, top = 3f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 12f))
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 12f)
        drawVerticalLine(color = palette.light, topLeft = Offset(2f, 2f), length = 7f)
        for (y in treadPattern..11 step 2) {
            drawPixel(color = palette.dark, topLeft = Offset(0f, y.toFloat()))
        }
        if (treadPattern == 0) {
            drawPixel(color = palette.medium, topLeft = Offset(0f, 0f))
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 0f), length = 2f)
        } else {
            drawHorizontalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 3f)
        }
    }

    // right tread
    translate(left = 11f, top = 3f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 12f))
        drawPixel(color = palette.light, topLeft = Offset(0f, 0f))
        drawRect(color = palette.dark, topLeft = Offset(0f, 1f), size = Size(2f, 11f))
        for (y in treadPattern..11 step 2) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, y.toFloat()), length = 2f)
        }
    }

    // body
    translate(left = 4f, top = 3f) {
        this as PixelDrawScope
        drawRect(color = palette.dark, topLeft = Offset(0f, 1f), size = Size(7f, 10f))
        drawRect(color = palette.dark, topLeft = Offset(1f, 0f), size = Size(5f, 2f))

        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 2f), length = 8f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(0f, 3f), length = 5f)
        drawVerticalLine(color = palette.light, topLeft = Offset(1f, 0f), length = 10f)
        drawRect(color = palette.medium, topLeft = Offset(1f, 2f), size = Size(5f, 7f))

        drawVerticalLine(color = palette.light, topLeft = Offset(2f, 3f), length = 5f)
        drawHorizontalLine(color = palette.medium, topLeft = Offset(2f, 9f), length = 3f)

        drawPixel(color = palette.light, topLeft = Offset(3f, 3f))
        drawPixel(color = palette.dark, topLeft = Offset(3f, 8f))

        drawVerticalLine(color = palette.dark, topLeft = Offset(4f, 4f), length = 5f)
        drawVerticalLine(color = palette.medium, topLeft = Offset(6f, 3f), length = 5f)
    }

    // barrel
    translate(left = 6f, top = 0f) {
        this as PixelDrawScope
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 2f)
        drawVerticalLine(color = palette.light, topLeft = Offset(1f, 0f), length = 5f)
        drawPixel(color = palette.medium, topLeft = Offset(1f, 1f))
        drawVerticalLine(color = palette.medium, topLeft = Offset(2f, 0f), length = 2f)
    }
}

fun PixelDrawScope.drawPlayerTankLevel4(treadPattern: Int, palette: TankColorPalette) {
    // left tread
    translate(left = 1f, top = 1f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 14f))
        drawVerticalLine(color = palette.light, topLeft = Offset(0f, 0f), length = 14f)
        drawPixel(color = palette.light, topLeft = Offset(2f, 1f))
        drawPixel(color = palette.dark, topLeft = Offset(2f, 12f))
        for (y in treadPattern..12 step 2) {
            drawPixel(color = palette.dark, topLeft = Offset(0f, 1f + y.toFloat()))
        }
        if (treadPattern == 0) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 13f), length = 2f)
        } else {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 0f), length = 2f)
            drawPixel(color = palette.medium, topLeft = Offset(0f, 0f))
        }
    }
    // right tread
    translate(left = 12f, top = 1f) {
        this as PixelDrawScope
        drawRect(color = palette.medium, topLeft = Offset(0f, 0f), size = Size(3f, 14f))
        drawPixel(color = palette.light, topLeft = Offset(0f, 0f))
        drawVerticalLine(color = palette.dark, topLeft = Offset(0f, 2f), length = 10f)
        drawPixel(color = palette.dark, topLeft = Offset(0f, 13f))
        drawPixel(color = palette.dark, topLeft = Offset(1f, 1f))
        drawPixel(color = palette.dark, topLeft = Offset(1f, 12f))
        for (y in treadPattern..12 step 2) {
            drawPixel(color = palette.dark, topLeft = Offset(2f, 1f + y.toFloat()))
        }
        if (treadPattern == 0) {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 13f), length = 2f)
        } else {
            drawHorizontalLine(color = palette.dark, topLeft = Offset(1f, 0f), length = 2f)
        }
    }
    // body
    translate(left = 4f, top = 2f) {
        this as PixelDrawScope
        drawRect(color = palette.light, topLeft = Offset(0f, 0f), size = Size(4f, 10f))
        drawVerticalLine(color = palette.medium, topLeft = Offset(2f, 0f), length = 3f)
        drawRect(color = palette.medium, topLeft = Offset(4f, 0f), size = Size(4f, 4f))
        drawVerticalLine(color = palette.dark, topLeft = Offset(5f, 0f), length = 3f)
        drawRect(color = palette.medium, topLeft = Offset(1f, 4f), size = Size(7f, 7f))
        drawRect(color = palette.light, topLeft = Offset(2f, 5f), size = Size(3f, 3f))
        drawRect(color = palette.dark, topLeft = Offset(3f, 6f), size = Size(3f, 3f))
        drawRect(color = palette.medium, topLeft = Offset(3f, 6f), size = Size(2f, 2f))

        drawVerticalLine(color = palette.dark, topLeft = Offset(7f, 3f), length = 7f)
        drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, 10f), length = 7f)
        drawHorizontalLine(color = palette.dark, topLeft = Offset(0f, 11f), length = 8f)
    }
    // barrel
    drawVerticalLine(color = palette.light, topLeft = Offset(7f, 0f), length = 4f)
    drawVerticalLine(color = palette.medium, topLeft = Offset(8f, 0f), length = 4f)

}


@Preview
@Composable
private fun PlayerTankPreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(500.dp), gridUnitNum = 8) {
            TankLevel.values().forEachIndexed { index, tankLevel ->
                TankTreadsPreview(
                    tank = Tank(
                        id = 0,
                        x = 0f.grid2mpx, y = index.toFloat().grid2mpx,
                        level = tankLevel,
                        side = TankSide.Player,
                        hp = 1,
                    ),
                    palette = PlayerYellowPalette
                )
                TankTreadsPreview(
                    tank = Tank(
                        id = 0,
                        x = 2f.grid2mpx, y = index.toFloat().grid2mpx,
                        level = tankLevel,
                        side = TankSide.Player,
                        hp = 1,
                    ),
                    palette = PlayerGreenPalette
                )
            }
        }
    }
}

@Composable
fun TankTreadsPreview(tank: Tank, palette: TankColorPalette = BotNormalPalette) {
    TankWithTreadPattern(tank = tank, treadPattern = 0, palette = palette)
    TankWithTreadPattern(tank = tank.copy(x = tank.x + 1f.grid2mpx), treadPattern = 1, palette = palette)
}