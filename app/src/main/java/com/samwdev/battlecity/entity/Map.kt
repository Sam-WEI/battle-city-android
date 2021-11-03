package com.samwdev.battlecity.entity

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.ui.components.mu

const val MAP_BLOCK_COUNT = 13

sealed class MapElement(open val index: Int, private val granularity: Int = 1) {
    val offset: IntOffset
        get() {
            val row = index / (granularity * MAP_BLOCK_COUNT)
            val col = index % (granularity * MAP_BLOCK_COUNT)
            return IntOffset(col, row)
        }

    val offsetInMapUnit: Offset get() {
        val (col, row) = offset
        val rowF = row / granularity.toFloat()
        val colF = col / granularity.toFloat()
        return Offset(colF, rowF)
    }

    val size: Dp @Composable get() = 1.mu / granularity.toFloat()
}

data class BrickElement(override val index: Int) : MapElement(index, 4) {

}

data class SteelElement(override val index: Int) : MapElement(index, 2) {

}

data class TreeElement(override val index: Int) : MapElement(index)
data class WaterElement(override val index: Int) : MapElement(index)
data class IceElement(override val index: Int) : MapElement(index)
data class EagleElement(override val index: Int) : MapElement(index)