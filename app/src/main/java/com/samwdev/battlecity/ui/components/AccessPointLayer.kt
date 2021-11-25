package com.samwdev.battlecity.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.core.grid2mpx

private val ColorGreen = Color(0x6600ff00)
private val ColorRed = Color(0x66ff0000)

@Composable
fun AccessPointLayer(mapState: MapState) {
    val points = mapState.accessPoints
    PixelCanvas(
        widthInMapPixel = MAP_BLOCK_COUNT.grid2mpx,
        heightInMapPixel = MAP_BLOCK_COUNT.grid2mpx,
    ) {
        for ((ri, row) in points.withIndex()) {
            for ((ci, value) in row.withIndex()) {
                drawCircle(
                    if (value > 0) ColorGreen else ColorRed,
                    radius = 2f,
                    center = Offset((ci / 2f + 0.5f).grid2mpx, (ri / 2f + 0.5f).grid2mpx)
                )
            }
        }
    }
}