package com.samwdev.battlecity.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.entity.MapConfig
import com.samwdev.battlecity.ui.component.maplayer.BrickLayer
import com.samwdev.battlecity.ui.component.maplayer.EagleLayer
import com.samwdev.battlecity.ui.component.maplayer.IceLayer
import com.samwdev.battlecity.ui.component.maplayer.SteelLayer
import com.samwdev.battlecity.ui.component.maplayer.TreeLayer
import com.samwdev.battlecity.ui.component.maplayer.WaterLayerFrame

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