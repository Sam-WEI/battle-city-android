package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun SpawnBlink(topLeft: Offset) {
    Framer(
        framesDef = listOf(100, 100, 100, 100),
        infinite = true,
        reverse = true,
    ) {
        val index = LocalFramer.current
        SpawnBlinkIndex(index = index, topLeft = topLeft)
    }
}

@Composable
fun SpawnBlinkIndex(index: Int, topLeft: Offset) {
    PixelCanvas(
        topLeftInMapPixel = topLeft,
        widthInMapPixel = 1f.cell2mpx,
        heightInMapPixel = 1f.cell2mpx,
    ) {
        when (index) {
            0 -> spawnBlink1()
            1 -> spawnBlink2()
            2 -> spawnBlink3()
            3 -> spawnBlink4()
        }
    }
}

private fun PixelDrawScope.spawnBlink1() {
    drawHorizontalLine(color = Color.White, topLeft = Offset(3f, 7f), length = 9f)
    drawVerticalLine(color = Color.White, topLeft = Offset(7f, 3f), length = 9f)
    drawRect(color = Color.White, topLeft = Offset(6f, 6f), size = Size(3f, 3f))
}

private fun PixelDrawScope.spawnBlink2() {
    drawHorizontalLine(color = Color.White, topLeft = Offset(2f, 7f), length = 11f)
    drawVerticalLine(color = Color.White, topLeft = Offset(7f, 2f), length = 11f)
    drawRect(color = Color.White, topLeft = Offset(6f, 5f), size = Size(3f, 5f))
    drawRect(color = Color.White, topLeft = Offset(5f, 6f), size = Size(5f, 3f))
}

private fun PixelDrawScope.spawnBlink3() {
    drawHorizontalLine(color = Color.White, topLeft = Offset(1f, 7f), length = 13f)
    drawVerticalLine(color = Color.White, topLeft = Offset(7f, 1f), length = 13f)
    drawRect(color = Color.White, topLeft = Offset(6f, 4f), size = Size(3f, 7f))
    drawRect(color = Color.White, topLeft = Offset(4f, 6f), size = Size(7f, 3f))
}

private fun PixelDrawScope.spawnBlink4() {
    drawHorizontalLine(color = Color.White, topLeft = Offset(0f, 7f), length = 15f)
    drawVerticalLine(color = Color.White, topLeft = Offset(7f, 0f), length = 15f)
    drawRect(color = Color.White, topLeft = Offset(6f, 3f), size = Size(3f, 9f))
    drawRect(color = Color.White, topLeft = Offset(3f, 6f), size = Size(9f, 3f))
    drawRect(color = Color.White, topLeft = Offset(5f, 5f), size = Size(5f, 5f))
}

@Preview
@Composable
fun SpawnBlinkPreview() {
    BattleCityTheme {
        Grid(
            modifier = Modifier.size(500.dp),
            gridSize = 4,
        ) {
            for (i in 0..3) {
                SpawnBlinkIndex(index = i, topLeft = Offset(0f, i.toFloat().cell2mpx))
            }
        }
    }
}