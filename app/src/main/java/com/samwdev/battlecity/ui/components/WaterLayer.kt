package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.core.PixelCanvas
import com.samwdev.battlecity.entity.WaterElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun WaterLayer(mapState: MapState) {
    mapState.waters.forEach { el ->
        WaterBlock(element = el)
    }
}

@Composable
private fun WaterBlock(element: WaterElement) {
    PixelCanvas() {

    }
}

@Preview
@Composable
fun WaterPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp)) {
            WaterBlock(element = WaterElement(0))
        }
    }
}