package com.samwdev.battlecity.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MAP_PIXEL_IN_EACH_BLOCK

sealed class MapElement(open val index: Int, private val granularity: Int = 1) {
    val gridPosition: IntOffset
        get() {
            val row = index / (granularity * MAP_BLOCK_COUNT)
            val col = index % (granularity * MAP_BLOCK_COUNT)
            return IntOffset(col, row)
        }

    val offsetInMapPixel: Offset get() {
        val (x, y) = gridPosition
        val rowF = y / granularity.toFloat() * MAP_PIXEL_IN_EACH_BLOCK
        val colF = x / granularity.toFloat() * MAP_PIXEL_IN_EACH_BLOCK
        return Offset(colF, rowF)
    }

    val elementSizeInMapPixel: Float get() = MAP_PIXEL_IN_EACH_BLOCK.toFloat() / granularity
}

data class BrickElement(override val index: Int) : MapElement(index, 4) {

}

data class SteelElement(override val index: Int) : MapElement(index, 2) {

}

data class TreeElement(override val index: Int) : MapElement(index)
data class WaterElement(override val index: Int) : MapElement(index)
data class IceElement(override val index: Int) : MapElement(index)
data class EagleElement(override val index: Int) : MapElement(index)