package com.samwdev.battlecity.ui.component.maplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.core.state.MapState
import com.samwdev.battlecity.ui.component.LocalGridSize
import com.samwdev.battlecity.ui.component.PixelCanvas

private val ColorGreen = Color(0xff00ff00)
private val ColorRed = Color(0xffff0000)

@Composable
fun AccessPointLayer(mapState: MapState) {
    val points = mapState.accessPoints
    val gridSize = LocalGridSize.current
    PixelCanvas(
        widthInMapPixel = gridSize.first.cell2mpx,
        heightInMapPixel = gridSize.second.cell2mpx,
    ) {
        for ((ri, row) in points.withIndex()) {
            for ((ci, value) in row.withIndex()) {
                val sg = SubGrid(ri, ci)
                drawCircle(
                    color = when {
                        points.isAccessible(sg) -> ColorGreen
                        points.isEagleArea(sg) -> Color.Magenta
                        else -> ColorRed
                    },
                    alpha = 0.4f,
                    radius = 1f,
                    center = Offset((ci / 2f + 0.5f).cell2mpx, (ri / 2f + 0.5f).cell2mpx)
                )
            }
        }
    }
}