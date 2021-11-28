package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.nextUp

@Composable
fun Hud(
    botCount: Int,
    lifeCount: Int,
    level: Int,
    hudLayoutOrientation: Orientation = Orientation.Horizontal,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .then(
                if (hudLayoutOrientation == Orientation.Horizontal) {
                    Modifier
                        .fillMaxWidth()
                        .height(1.grid2mpx.mpx2dp)
                } else {
                    Modifier
                        .fillMaxHeight()
                        .width(1.grid2mpx.mpx2dp)
                }
            )
    ) {
        BotIcon(botCount, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun BotIcon(botCount: Int, modifier: Modifier) {
    val rowMax = if (botCount <= 20) 10 else 20
    PixelCanvas(
        widthInMapPixel = 5f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
        modifier = modifier,
    ) {
        val topPadding = (4 - (botCount / rowMax.toFloat()).nextUp()) / 4f
        repeat(botCount) { i ->
            val row = i / rowMax
            val col = i % rowMax
            scale(0.5f, pivot = Offset(0f, topPadding.grid2mpx)) {
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