package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.ui.theme.BattleCityTheme


@Composable
fun BrickLayer(mapState: MapState) {
    mapState.bricks.forEach { el ->
        BrickBlock(element = el)
    }
}

@Composable
private fun BrickBlock(element: BrickElement) {

}

@Preview
@Composable
fun BrickPreview() {
    BattleCityTheme {
        Map(modifier = Modifier.size(500.dp)) {
            BrickBlock(element = BrickElement(0))
        }
    }
}