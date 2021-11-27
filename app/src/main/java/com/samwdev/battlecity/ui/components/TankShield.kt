package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun TankShield(topLeft: Offset) {
    Framer(framesDef = listOf(50, 50), infinite = true) {
        TankShield(topLeft, LocalFramer.current)
    }
}

@Composable
private fun TankShield(topLeft: Offset, frame: Int) {
    PixelCanvas(
        topLeftInMapPixel = topLeft,
        widthInMapPixel = 1f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
    ) {
        repeat(4) { i ->
            rotate(
                degrees = 360f / 4f * i,
                pivot = Offset(8f, 8f),
            ) {
                this as PixelDrawScope
                drawShieldPetal(frame)
                scale(scaleX = -1f, scaleY = 1f, pivot = Offset(8f, 8f)) {
                    this as PixelDrawScope
                    drawShieldPetal(frame)
                }
            }
        }
    }
}

private fun PixelDrawScope.drawShieldPetal(frame: Int) {
    if (frame == 0) {
        drawVerticalLine(color = Color.White, topLeft = Offset(4f, 2f), length = 2f)
        drawPixel(color = Color.White, topLeft = Offset(5f, 1f))
        drawHorizontalLine(color = Color.White, topLeft = Offset(6f, 0f), length = 2f)
    } else {
        drawPixel(color = Color.White, topLeft = Offset(1f, 1f))
        drawHorizontalLine(color = Color.White, topLeft = Offset(2f, 0f), length = 2f)
        drawPixel(color = Color.White, topLeft = Offset(4f, 1f))
        drawHorizontalLine(color = Color.White, topLeft = Offset(5f, 2f), length = 2f)
        drawPixel(color = Color.White, topLeft = Offset(7f, 3f))
    }
}

@Preview
@Composable
private fun TankShieldPreview() {
    BattleCityTheme {
        Grid(modifier = Modifier.size(200.dp), gridUnitNum = 2) {
            TankShield(Offset(0f.grid2mpx, 0f.grid2mpx), 0)
            TankShield(Offset(0f.grid2mpx, 1f.grid2mpx), 1)
        }
    }
}