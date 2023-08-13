package com.samwdev.battlecity.core

import androidx.compose.ui.geometry.Rect
import com.samwdev.battlecity.entity.MapElementHelper

interface HorizontalGridSizeAware {
    val hGridSize: Int

    fun MapElementHelper.overlapsAnyElement(realElements: Set<Int>, subGrid: SubGrid, hSpan: Int = 1, vSpan: Int = 1): Boolean =
        overlapsAnyElement(realElements, subGrid, hGridSize, hSpan, vSpan)

    fun MapElementHelper.getSubGrid(index: Int): SubGrid = getSubGrid(index, hGridSize)

    fun MapElementHelper.getIndicesOverlappingRect(rect: Rect, moveDirection: Direction = Direction.Down): List<Int> =
        getOverlapIndicesInRect(rect, hGridSize, moveDirection)
}

interface VerticalGridSizeAware {
    val vGridSize: Int
}

interface GridSizeAware : HorizontalGridSizeAware, VerticalGridSizeAware