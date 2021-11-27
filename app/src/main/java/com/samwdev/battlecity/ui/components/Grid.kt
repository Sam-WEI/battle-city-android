package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx


@Composable
fun Grid(
    modifier: Modifier,
    gridUnitNum: Int = MAP_BLOCK_COUNT,
    hGridUnitNum: Int = gridUnitNum,
    vGridUnitNum: Int = gridUnitNum,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val mapPixelInDp = remember(maxWidth) { maxWidth / (gridUnitNum.grid2mpx) }
        CompositionLocalProvider(
            LocalMapPixelDp provides mapPixelInDp,
            LocalGridUnitNumber provides (hGridUnitNum to vGridUnitNum)
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
    error("Not in Map composable or its child composable.")
}

/**
 * Provides dp size for one MapPixel
 */
val LocalGridUnitNumber = staticCompositionLocalOf<Pair<Int, Int>> {
    error("Not in Map composable or its child composable.")
}
