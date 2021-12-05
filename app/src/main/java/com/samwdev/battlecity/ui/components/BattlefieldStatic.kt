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
        hGridUnitNum = mapConfig.hGridUnitNum,
        vGridUnitNum = mapConfig.vGridUnitNum,
    ) {
        BrickLayer(mapConfig.bricks)
        SteelLayer(mapConfig.steels)
        IceLayer(mapConfig.ices)
        WaterLayer(mapConfig.waters)
        EagleLayer(mapConfig.eagle)
        TreeLayer(mapConfig.trees)
    }
}