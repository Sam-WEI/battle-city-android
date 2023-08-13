package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.entity.MapConfig

@Composable
fun BattlefieldStatic(mapConfig: MapConfig, modifier: Modifier = Modifier) {
    Grid(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Black),
        hGridSize = mapConfig.hGridSize,
        vGridSize = mapConfig.vGridSize,
    ) {
        BrickLayer(mapConfig.bricks)
        SteelLayer(mapConfig.steels)
        IceLayer(mapConfig.ices)
        WaterLayerFrame(mapConfig.waters, 0)
        EagleLayer(mapConfig.eagle)
        TreeLayer(mapConfig.trees)
    }
}