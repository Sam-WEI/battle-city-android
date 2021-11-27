package com.samwdev.battlecity.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.MapState
import com.samwdev.battlecity.core.grid2mpx

private val ColorGreen = Color(0xff00ff00)
private val ColorRed = Color(0xffff0000)

@Composable
fun AccessPointLayer(mapState: MapState) {
    val points = mapState.accessPoints
    val gridUnitNumber = LocalGridUnitNumber.current
    PixelCanvas(
        widthInMapPixel = gridUnitNumber.grid2mpx,
        heightInMapPixel = gridUnitNumber.grid2mpx,
    ) {
        for ((ri, row) in points.withIndex()) {
            for ((ci, value) in row.withIndex()) {
                drawCircle(
                    color = when {
                        value > 0 -> ColorGreen
                        value == -1 -> Color.White
                        else -> ColorRed
                    },
                    alpha = 0.4f,
                    radius = 1f,
                    center = Offset((ci / 2f + 0.5f).grid2mpx, (ri / 2f + 0.5f).grid2mpx)
                )
            }
        }
    }
}