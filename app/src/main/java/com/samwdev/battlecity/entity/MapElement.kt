package com.samwdev.battlecity.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx
import kotlin.math.ceil

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
        return Offset(colF.grid2mpx, rowF.grid2mpx)
    }

    val elementSize: MapPixel get() = 1.grid2mpx / granularity
}

data class BrickElement(override val index: Int) : MapElement(index, 4) {
    val patternIndex: Int get() = (gridPosition.x + gridPosition.y) % 2

    companion object : MapElementHelper(4)
}

data class SteelElement(override val index: Int) : MapElement(index, 2) {
    companion object : MapElementHelper(2)
}

data class TreeElement(override val index: Int) : MapElement(index) {
    companion object : MapElementHelper()
}

data class WaterElement(override val index: Int) : MapElement(index) {
    companion object : MapElementHelper()
}

data class IceElement(override val index: Int) : MapElement(index) {
    companion object : MapElementHelper()
}

data class EagleElement(override val index: Int) : MapElement(index) {
    companion object : MapElementHelper()
}

open class MapElementHelper(private val granularity: Int = 1) {
    val elementSize: MapPixel get() = 1.grid2mpx / granularity
    val countInOneLine: Int get() = MAP_BLOCK_COUNT * granularity

    fun getIndicesInRect(rect: Rect, moveDirection: Direction): List<Int> {
        val top: MapPixel = rect.top
        val right: MapPixel = rect.right
        val bottom: MapPixel = rect.bottom
        val left: MapPixel = rect.left

        val col1 = (left / elementSize).toInt()
        val row1 = (top / elementSize).toInt()
        val col2 = ceil(right / elementSize).toInt()
        val row2 = ceil(bottom / elementSize).toInt()

        val firstDimen: IntProgression
        val secondDimen: IntProgression

        val ret = mutableListOf<Int>()
        when (moveDirection) {
            Direction.Up -> {
                firstDimen = row2 downTo row1
                secondDimen = col1..col2
            }
            Direction.Down -> {
                firstDimen = row1..row2
                secondDimen = col1..col2
            }
            Direction.Left -> {
                firstDimen = col2 downTo col1
                secondDimen = row1..row2
            }
            Direction.Right -> {
                firstDimen = col1..col2
                secondDimen = row1..row2
            }
        }
        if (moveDirection.isHorizontal()) {
            for (r in firstDimen) {
                for (c in secondDimen) {
                    ret.add(c * countInOneLine + r)
                }
            }
        } else {
            for (r in firstDimen) {
                for (c in secondDimen) {
                    ret.add(r * countInOneLine + c)
                }
            }
        }
        return ret
    }
}