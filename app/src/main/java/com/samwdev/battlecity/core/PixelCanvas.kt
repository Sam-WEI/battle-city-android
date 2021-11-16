package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.samwdev.battlecity.ui.components.LocalMapPixelDp
import com.samwdev.battlecity.ui.components.mpx2dp

/**
 * This canvas allows you to draw in original game pixel
 */
@Composable
fun PixelCanvas(
    modifier: Modifier = Modifier,
    widthInMapPixel: MapPixel = 1f.grid2mpx,
    heightInMapPixel: MapPixel = 1f.grid2mpx,
    topLeftInMapPixel: Offset = Offset.Zero,
    onDraw: DrawScope.() -> Unit
) {
    val mpxDp = LocalMapPixelDp.current
    Canvas(
        modifier = Modifier
            .size(widthInMapPixel.mpx2dp, heightInMapPixel.mpx2dp)
            .offset(topLeftInMapPixel.x.mpx2dp, topLeftInMapPixel.y.mpx2dp)
            .then(modifier)
    ) {
        scale(mpxDp.toPx(), pivot = Offset.Zero) {
            this.onDraw()
        }
    }
}