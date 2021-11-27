package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun Hud(
    botCount: Int,
    lifeCount: Int,
    level: Int,
    hudLayoutOrientation: Orientation = Orientation.Horizontal,
) {
    Box(
        modifier = Modifier
            .then(
                if (hudLayoutOrientation == Orientation.Horizontal) {
                    Modifier
                        .fillMaxWidth()
                        .height(2f.grid2mpx.mpx2dp)
                } else {
                    Modifier
                        .fillMaxHeight()
                        .width(2f.grid2mpx.mpx2dp)
                }
            )
    ) {
        BotIcon(botCount)
    }
}

@Composable
fun BotIcon(botCount: Int) {
    PixelCanvas(
        topLeftInMapPixel = Offset(0.5f.grid2mpx, 0.5f.grid2mpx),
        widthInMapPixel = 5f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
    ) {
        repeat(botCount) { i ->
            val row = i / 10
            val col = i % 10
            translate(left = col * 0.5f.grid2mpx, top = row * 0.5f.grid2mpx) {
                this as PixelDrawScope
                drawVerticalLine(
                    color = Color.Black,
                    topLeft = Offset(0f, 0f),
                    length = 6f
                )
                drawVerticalLine(
                    color = Color.Black,
                    topLeft = Offset(3f, 0f),
                    length = 6f
                )
                drawVerticalLine(
                    color = Color.Black,
                    topLeft = Offset(6f, 0f),
                    length = 6f
                )
                drawHorizontalLine(
                    color = Color.Black,
                    topLeft = Offset(2f, 6f),
                    length = 3f
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(1f, 2f),
                    size = Size(5f, 2f)
                )
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(2f, 1f),
                    size = Size(3f, 4f)
                )
                drawVerticalLine(
                    color = Color(97, 20, 9),
                    topLeft = Offset(3f, 2f),
                    length = 2f
                )
            }
        }
    }
}

@Preview
@Composable
fun HudPreview() {
    BattleCityTheme {
        Grid(
            modifier = Modifier
                .width(500.dp)
                .height(200.dp)
                .background(Color(117, 117, 117)),
            gridUnitNum = MAP_BLOCK_COUNT
        ) {
            Hud(botCount = 20, lifeCount = 3, level = 30)
        }
    }
}