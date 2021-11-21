package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.roundToInt


private const val TreadPatternCycleDistance = 5

@Composable
fun Tank(tank: Tank) {
    var travelDistance by remember { mutableStateOf(0f) }
    val treadPattern: Int by remember(travelDistance) {
        derivedStateOf {
            // calculate tread pattern based on travel distance, so faster tank looks faster
            if (travelDistance.roundToInt() % (TreadPatternCycleDistance * 2) < TreadPatternCycleDistance) 0 else 1
        }
    }
    travelDistance = remember(tank.x, tank.y, tank.direction) {
        travelDistance + tank.speed * 10
    }
    if (LocalDebugConfig.current.showPivotBox) {
        PixelCanvas(
            modifier = Modifier.clipToBounds(),
            topLeftInMapPixel = Offset(tank.pivotBox.left, tank.pivotBox.top),
            widthInMapPixel = tank.pivotBox.width.grid2mpx,
            heightInMapPixel = tank.pivotBox.height.grid2mpx
        ) {
            drawSquare(Color(0x55ffffff), topLeft = Offset.Zero, side = tank.pivotBox.width)
        }
    }

    if (tank.timeToSpawn <= 0) {
        if (tank.side == TankSide.Bot && tank.level == TankLevel.Level4 && tank.hp != 1) {
            Framer(framesDef = listOf(40, 40), infinite = true) {
                val frame = LocalFramer.current
                TankWithTreadPattern(tank = tank, treadPattern = treadPattern, frame = frame)
            }
        } else {
            TankWithTreadPattern(tank = tank, treadPattern = treadPattern)
        }
    } else {
        SpawnBlink(topLeft = tank.offset)
    }
}

@Composable
fun TankWithTreadPattern(tank: Tank, treadPattern: Int, frame: Int = 0) {
    PixelCanvas(
        modifier = when (tank.direction) {
            Direction.Up -> Modifier
            Direction.Down -> Modifier.scale(1f, -1f)
            Direction.Left -> Modifier
                .scale(1f, -1f)
                .rotate(Direction.Left.degree)
            Direction.Right -> Modifier.rotate(Direction.Right.degree)
        },
        topLeftInMapPixel = Offset(tank.x, tank.y),
        widthInMapPixel = TANK_MAP_PIXEL,
        heightInMapPixel = TANK_MAP_PIXEL
    ) {
        when (tank.side) {
            TankSide.Player -> {
                when (tank.level) {
                    TankLevel.Level1 -> drawPlayerTankLevel1(treadPattern)
                    TankLevel.Level2 -> drawPlayerTankLevel1(treadPattern)
                    TankLevel.Level3 -> drawPlayerTankLevel1(treadPattern)
                    TankLevel.Level4 -> drawPlayerTankLevel1(treadPattern)
                }
            }
            TankSide.Bot -> {
                when (tank.level) {
                    TankLevel.Level1 -> drawBotTankLevel1(treadPattern)
                    TankLevel.Level2 -> drawBotTankLevel2(treadPattern)
                    TankLevel.Level3 -> drawBotTankLevel3(treadPattern)
                    TankLevel.Level4 -> drawBotTankLevel4(treadPattern, tank.hp, frame)
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
            Map(modifier = Modifier.size(500.dp), sideBlockCount = 8) {
                for (i in 0 until 7) {
                    val x = 3f + 0.2f * i
                    val y = i.toFloat()
                    val tank = Tank(
                        id = 1,
                        x = x.grid2mpx,
                        y = y.grid2mpx,
                        direction = Direction.Right,
                        side = TankSide.Player,
                        hp = 1)
                    Tank(tank)
                    Text(text = "tank x = $x", Modifier.offset((x + 1).grid2mpx.mpx2dp, y.grid2mpx.mpx2dp))
                    Text(text = "pivot x = ${tank.pivotBox.left / 1f.grid2mpx}", Modifier.offset((x + 1).grid2mpx.mpx2dp, (y + 0.5f).grid2mpx.mpx2dp))
                }
                Tank(Tank(id = 0, x = 0f.grid2mpx, y = 0f.grid2mpx, side = TankSide.Player, level = TankLevel.Level1, hp = 1, direction = Direction.Up))
                Tank(Tank(id = 0, x = 1f.grid2mpx, y = 0f.grid2mpx, side = TankSide.Bot, level = TankLevel.Level1, hp = 1, direction = Direction.Up))

            }
        }
    }
}