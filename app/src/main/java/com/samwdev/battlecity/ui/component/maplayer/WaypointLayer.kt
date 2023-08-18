package com.samwdev.battlecity.ui.component.maplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.samwdev.battlecity.core.state.BotState
import com.samwdev.battlecity.core.SubGrid
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.ui.component.LocalGridSize
import com.samwdev.battlecity.ui.component.PixelCanvas
import com.samwdev.battlecity.ui.component.PixelDrawScope

private val ColorList = listOf(
    Color(0xFF00ffff),
    Color(0xFFFFEB3B),
    Color(0xFF673AB7),
    Color(0xFFF44336),
    Color(0xFF8BC34A),
    Color(0xFF004D40),
    Color(0xFFFFFFFF),
)


@Composable
fun WaypointLayer(botState: BotState) {
    val waypointsList = botState.bots.values.map { it.currentWaypoint }
    val gridSize = LocalGridSize.current
    PixelCanvas(
        widthInMapPixel = gridSize.first.cell2mpx,
        heightInMapPixel = gridSize.second.cell2mpx,
    ) {
        waypointsList.forEachIndexed { i, waypoints ->
            val color = ColorList[i % ColorList.size]
            drawWaypoints(waypoints, color)
        }
    }
}

private fun PixelDrawScope.drawWaypoints(waypoints: List<SubGrid>, color: Color) {
    for ((i, wp) in waypoints.withIndex()) {
        val center = Offset(wp.x + 0.5f.cell2mpx, wp.y + 0.5f.cell2mpx)
        if (i == 0 || i == waypoints.lastIndex) {
            drawCircle(
                color,
                alpha = 0.8f,
                radius = 2f,
                center = center,
            )
        }
        if (i > 0) {
            val lastWp = waypoints[i - 1]
            val lastDotCenter = Offset(lastWp.x + 0.5f.cell2mpx, lastWp.y + 0.5f.cell2mpx)
            drawLine(
                color,
                alpha = 0.6f,
                start = lastDotCenter,
                end = center,
                strokeWidth = 1f,
                cap = StrokeCap.Round,
            )
        }
    }
}