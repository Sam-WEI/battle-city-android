package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.PixelCanvas
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun SpawnBlink() {

}

@Composable
fun SpawnBlinkSmall(topLeft: Offset) {
    PixelCanvas(
        widthInMapPixel = 1f.grid2mpx,
        heightInMapPixel = 1f.grid2mpx,
        topLeftInMapPixel = topLeft,
    ) {

    }
}

@Preview
@Composable
fun SpawnBlinkPreview() {
    BattleCityTheme {
        Map(
            modifier = Modifier.size(500.dp),
            sideBlockCount = 4,
        ) {
            SpawnBlink()
        }
    }
}