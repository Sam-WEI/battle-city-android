package com.samwdev.battlecity.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.samwdev.battlecity.core.BotState
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.SubGrid
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.utils.logW

private val ColorDot = Color(0xBB00ffff)
private val ColorLine = Color(0x6600ffff)

@Composable
fun AccessPointLayer(botState: BotState) {
    val waypointsList = remember(botState.bots) {
        botState.bots.values.map { it.botAi.currentWaypoint }
    }
    PixelCanvas(
        widthInMapPixel = MAP_BLOCK_COUNT.grid2mpx,
        heightInMapPixel = MAP_BLOCK_COUNT.grid2mpx,
    ) {
        waypointsList.forEach { waypoints ->
            drawWaypoints(waypoints)
        }
    }
}

private fun PixelDrawScope.drawWaypoints(waypoints: List<SubGrid>) {
    for ((i, wp) in waypoints.withIndex()) {
        val center = Offset(wp.x + 0.5f.grid2mpx, wp.y + 0.5f.grid2mpx)
        drawCircle(
            ColorDot,
            radius = 3f,
            center = center,
        )
        if (i > 0) {
            val lastWp = waypoints[i - 1]
            val lastDotCenter = Offset(lastWp.x + 0.5f.grid2mpx, lastWp.y + 0.5f.grid2mpx)
            drawLine(
                ColorLine,
                start = lastDotCenter,
                end = center,
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
            )
        }
    }
}