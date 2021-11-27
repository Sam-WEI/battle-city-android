package com.samwdev.battlecity.core

import androidx.compose.ui.geometry.Rect
import com.samwdev.battlecity.entity.MapElementHelper

interface HorizontalGridUnitNumberAware {
    val hGridUnitNum: Int

    fun MapElementHelper.overlapsAnyElement(realElements: Set<Int>, subGrid: SubGrid, hSpan: Int = 1, vSpan: Int = 1): Boolean =
        overlapsAnyElement(realElements, subGrid, hGridUnitNum, hSpan, vSpan)

    fun MapElementHelper.getSubGrid(index: Int): SubGrid = getSubGrid(index, hGridUnitNum)

    fun MapElementHelper.getIndicesOverlappingRect(rect: Rect, moveDirection: Direction = Direction.Down): List<Int> =
        getIndicesOverlappingRect(rect, hGridUnitNum, moveDirection)
}