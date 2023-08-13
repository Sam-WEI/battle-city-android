package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.roundToInt


private const val TreadPatternCycleDistance = 5

val PlayerYellowPalette = TankColorPalette(Color(227, 227, 137), Color(227, 145, 30), Color(96, 96, 0))
val PlayerGreenPalette = TankColorPalette(Color(172, 246, 199), Color(0, 129, 43), Color(0, 72, 0))

val BotNormalPalette = TankColorPalette(Color.White, Color(163, 163, 163), Color(0, 58, 65))
val BotPowerUpPalette = TankColorPalette(Color.White, Color(172, 43, 30), Color(80, 0, 112))

val BotLevel4GreenPalette = PlayerGreenPalette
val BotLevel4YellowPalette = PlayerYellowPalette

private val BotLevel4HpToFlashingPaletteMap = mapOf(
    1 to listOf(BotNormalPalette, BotNormalPalette),
    2 to listOf(BotLevel4GreenPalette, BotLevel4YellowPalette),
    3 to listOf(BotNormalPalette, BotLevel4YellowPalette),
    4 to listOf(BotNormalPalette, BotLevel4GreenPalette),
)

data class TankColorPalette(val light: Color, val medium: Color, val dark: Color)

@Composable
fun Tank(tank: Tank) {
    var travelDistance by remember { mutableFloatStateOf(0f) }
    val treadPattern: Int by remember(travelDistance) {
        derivedStateOf {
            // calculate tread pattern based on travel distance, so faster tank looks faster
            if (travelDistance.roundToInt() % (TreadPatternCycleDistance * 2) < TreadPatternCycleDistance) 0 else 1
        }
    }
    travelDistance = remember(tank.x, tank.y, tank.movingDirection) {
        travelDistance + tank.currentSpeed * 10
    }
    if (LocalDebugConfig.current.showPivotBox) {
        PixelCanvas(
            modifier = Modifier.clipToBounds(),
            topLeftInMapPixel = Offset(tank.pivotBox.left, tank.pivotBox.top),
            widthInMapPixel = tank.pivotBox.width.cell2mpx,
            heightInMapPixel = tank.pivotBox.height.cell2mpx
        ) {
            drawSquare(Color(0x5599ffff), topLeft = Offset.Zero, side = tank.pivotBox.width)
        }
    }

    if (tank.timeToSpawn <= 0) {
        when (tank.side) {
            TankSide.Player -> {
                TankWithTreadPattern(tank = tank, treadPattern = treadPattern, PlayerYellowPalette)
                if (tank.hasShield) {
                    TankShield(topLeft = tank.offset)
                }
            }
            TankSide.Bot -> {
                if (tank.hasPowerUp) {
                    Framer(framesDef = listOf(140, 140), infinite = true) {
                        TankWithTreadPattern(
                            tank = tank,
                            treadPattern = treadPattern,
                            palette = if (LocalFramer.current == 0) { BotNormalPalette } else { BotPowerUpPalette }
                        )
                    }
                } else if (tank.level == TankLevel.Level4 && tank.hp > 1) {
                    Framer(framesDef = listOf(40, 40), infinite = true) {
                        val hp = tank.hp.coerceIn(1..4)
                        TankWithTreadPattern(
                            tank = tank,
                            treadPattern = treadPattern,
                            palette = BotLevel4HpToFlashingPaletteMap.getValue(hp)[LocalFramer.current]
                        )
                    }
                } else {
                    TankWithTreadPattern(tank = tank, treadPattern = treadPattern, BotNormalPalette)
                }
            }
        }
    } else {
        SpawnBlink(topLeft = tank.offset)
    }
}

@Composable
fun TankWithTreadPattern(tank: Tank, treadPattern: Int, palette: TankColorPalette) {
    PixelCanvas(
        topLeftInMapPixel = Offset(tank.x, tank.y),
        widthInMapPixel = TANK_MAP_PIXEL,
        heightInMapPixel = TANK_MAP_PIXEL
    ) {
        drawForDirection(tank.facingDirection, pivot = Offset(TANK_MAP_PIXEL / 2, TANK_MAP_PIXEL / 2)) {
            this as PixelDrawScope
            when (tank.side) {
                TankSide.Player -> {
                    when (tank.level) {
                        TankLevel.Level1 -> drawPlayerTankLevel1(treadPattern, palette)
                        TankLevel.Level2 -> drawPlayerTankLevel2(treadPattern, palette)
                        TankLevel.Level3 -> drawPlayerTankLevel3(treadPattern, palette)
                        TankLevel.Level4 -> drawPlayerTankLevel4(treadPattern, palette)
                    }
                }
                TankSide.Bot -> {
                    when (tank.level) {
                        TankLevel.Level1 -> drawBotTankLevel1(treadPattern, palette)
                        TankLevel.Level2 -> drawBotTankLevel2(treadPattern, palette)
                        TankLevel.Level3 -> drawBotTankLevel3(treadPattern, palette)
                        TankLevel.Level4 -> drawBotTankLevel4(treadPattern, palette)
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TankPreview() {
    BattleCityTheme {
        CompositionLocalProvider(LocalDebugConfig provides DebugConfig(showPivotBox = true)) {
            Grid(modifier = Modifier
                .size(500.dp)
                .background(Color.DarkGray), gridSize = 8) {
                for (i in 0 until 7) {
                    val x = 3f + 0.2f * i
                    val y = i.toFloat()
                    val tank = Tank(
                        id = 1,
                        x = x.cell2mpx,
                        y = y.cell2mpx,
                        facingDirection = Direction.Right,
                        side = TankSide.Player,
                        hp = 1,
                    )
                    Tank(tank)
                    Text(text = "tank x = $x", Modifier.offset((x + 1).cell2mpx.mpx2dp, y.cell2mpx.mpx2dp))
                    Text(text = "pivot x = ${tank.pivotBox.left / 1f.cell2mpx}", Modifier.offset((x + 1).cell2mpx.mpx2dp, (y + 0.5f).cell2mpx.mpx2dp))
                }
                TankLevel.values().forEachIndexed { index, tankLevel ->
                    Tank(Tank(
                        id = 0,
                        x = 0f.cell2mpx,
                        y = index.toFloat().cell2mpx,
                        side = TankSide.Player,
                        level = tankLevel,
                        hp = 1,
                        facingDirection = Direction.Up)
                    )
                    Tank(Tank(
                        id = 0,
                        x = 1f.cell2mpx,
                        y = index.toFloat().cell2mpx,
                        side = TankSide.Bot,
                        level = tankLevel,
                        hp = 1,
                        facingDirection = Direction.Up)
                    )
                }
            }
        }
    }
}