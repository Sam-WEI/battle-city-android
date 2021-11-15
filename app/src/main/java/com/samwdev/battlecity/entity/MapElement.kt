package com.samwdev.battlecity.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.mpx

sealed class MapElement(open val index: Int, private val granularity: Int = 1) {
    val gridPosition: IntOffset
        get() {
            val row = index / (granularity * MAP_BLOCK_COUNT)
            val col = index % (granularity * MAP_BLOCK_COUNT)
            return IntOffset(col, row)
        }

    val offsetInMapPixel: Offset get() {
        val (x, y) = gridPosition
        val rowF = y / granularity.toFloat()
        val colF = x / granularity.toFloat()
        return Offset(colF.mpx, rowF.mpx)
    }

    val elementSizeInMapPixel: Float get() = 1.mpx.toFloat() / granularity
}

data class BrickElement(override val index: Int) : MapElement(index, 4) {

}

data class SteelElement(override val index: Int) : MapElement(index, 2) {

}

data class TreeElement(override val index: Int) : MapElement(index)
data class WaterElement(override val index: Int) : MapElement(index)
data class IceElement(override val index: Int) : MapElement(index)
data class EagleElement(override val index: Int) : MapElement(index)