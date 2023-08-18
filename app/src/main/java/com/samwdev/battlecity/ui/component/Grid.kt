package com.samwdev.battlecity.ui.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.samwdev.battlecity.core.MAP_GRID_SIZE
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.cell2mpx


@Composable
fun Grid(
    modifier: Modifier,
    gridSize: Int = MAP_GRID_SIZE,
    hGridSize: Int = gridSize,
    vGridSize: Int = gridSize,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val mapPixelInDp = remember(maxWidth) { maxWidth / (hGridSize.cell2mpx) }
        CompositionLocalProvider(
            LocalMapPixelDp provides mapPixelInDp,
            LocalGridSize provides (hGridSize to vGridSize)
        ) {
            content()
        }
    }
}

/** MapPixel to Dp */
val MapPixel.mpx2dp: Dp @Composable get() = LocalMapPixelDp.current * this

/**
 * Provides dp size for one MapPixel
 */
val LocalMapPixelDp = staticCompositionLocalOf<Dp> {
    error("Not in Grid composable or its child composable.")
}

/**
 * Provides dp size for one MapPixel
 */
val LocalGridSize = staticCompositionLocalOf<Pair<Int, Int>> {
    error("Not in Grid composable or its child composable.")
}
