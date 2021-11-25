package com.samwdev.battlecity.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx

sealed class MapElement(open val index: Int) : MapElementProperties {
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

    val rect: Rect get() = Rect(offset = offsetInMapPixel, size = Size(elementSize, elementSize))
}

data class BrickElement(override val index: Int) : MapElement(index), MapElementProperties by BrickElement {
    val patternIndex: Int get() = (gridPosition.x + gridPosition.y) % 2

    companion object : MapElementHelper(4) {
        operator fun invoke(row: Int, col: Int) = BrickElement(getIndex(row, col))
    }
}

data class SteelElement(override val index: Int) : MapElement(index), MapElementProperties by SteelElement {
    companion object : MapElementHelper(2) {
        override val strength: Int = 3
        operator fun invoke(row: Int, col: Int) = SteelElement(getIndex(row, col))
    }
}

data class TreeElement(override val index: Int) : MapElement(index), MapElementProperties by TreeElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int) = TreeElement(getIndex(row, col))
    }
}

data class WaterElement(override val index: Int) : MapElement(index), MapElementProperties by WaterElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int) = WaterElement(getIndex(row, col))
    }
}

data class IceElement(override val index: Int) : MapElement(index), MapElementProperties by IceElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int) = IceElement(getIndex(row, col))
    }
}

data class EagleElement(override val index: Int, val dead: Boolean = false) : MapElement(index), MapElementProperties by EagleElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, destroyed: Boolean = false) = EagleElement(getIndex(row, col), destroyed)
    }
}

open class MapElementHelper(override val granularity: Int) : MapElementProperties {
    fun getRectByIndex(index: Int): Rect {
        val row = index / (countInOneLine)
        val col = index % (countInOneLine)
        return Rect(
            offset = Offset(col * elementSize, row * elementSize),
            size = Size(elementSize, elementSize),
        )
    }

    fun overlapsAnyElement(realElements: Set<Int>, subRow: Int, subCol: Int): Boolean {
        // 2 is the sub granularity
        val realRow = (subRow / (2f / granularity)).toInt()
        val realCol = (subCol / (2f / granularity)).toInt()

        for (i in 0 until granularity) {
            for (j in 0 until granularity) {
                val idx = getIndex(realRow + i, realCol + j)
                if (idx in realElements) return true
            }
        }
        return false
    }

    protected fun getIndex(row: Int, col: Int) = row * countInOneLine + col

    /**
     * This method returns the indices of the elements in the rect regardless of there actually is any elements in the rect.
     * @param moveDirection determines how the returned items are traversed. First hit items come first.
     */
    fun getIndicesOverlappingRect(rect: Rect, moveDirection: Direction = Direction.Down): List<Int> {
        val top: MapPixel = rect.top
        val bottom: MapPixel = rect.bottom - 0.1f // exclude the bottom border
        val left: MapPixel = rect.left
        val right: MapPixel = rect.right - 0.1f // exclude the right border

        val col1 = (left / elementSize).toInt().coerceAtLeast(0)
        val row1 = (top / elementSize).toInt().coerceAtLeast(0)
        val col2 = (right / elementSize).toInt().coerceAtMost(countInOneLine - 1)
        val row2 = (bottom / elementSize).toInt().coerceAtMost(countInOneLine - 1)

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
            for (c in firstDimen) {
                for (r in secondDimen) {
                    ret.add(getIndex(r, c))
                }
            }
        } else {
            for (r in firstDimen) {
                for (c in secondDimen) {
                    ret.add(getIndex(r, c))
                }
            }
        }
        return ret
    }

    fun getHitPoint(
        realElement: MapElement,
        trajectory: Rect,
        direction: Direction,
    ): Offset? = getHitPoint(setOf(realElement), trajectory, direction)

    /**
     * Get the first actual hit point along the trajectory
     */
    fun getHitPoint(
        realElements: Set<MapElement>,
        trajectory: Rect,
        direction: Direction,
    ): Offset? {
        val indicesAlongTrajectory = getIndicesOverlappingRect(trajectory, moveDirection = direction)
        val realSet = realElements.map { it.index }.toSet()

        // the first matching element is guaranteed to be one of the first hit elements in the direction,
        // so we can use it to get the hit point's dimension across the bullet flying direction,
        // the other dimension will be the bullet's center.
        // e.g, for a bullet flying right, the "left" of the first hit elements shares the x of the hit point,
        // the bullet center's y is the y of the hit point.
        val firstHitIndex = indicesAlongTrajectory.find { it in realSet } ?: return null
        val (leftMost, topMost, rightMost, bottomMost) = getRectByIndex(firstHitIndex)

        return when (direction) {
            Direction.Up -> {
                Offset(trajectory.center.x, bottomMost)
            }
            Direction.Down -> {
                Offset(trajectory.center.x, topMost)
            }
            Direction.Left -> {
                Offset(rightMost, trajectory.center.y)
            }
            Direction.Right -> {
                Offset(leftMost,  trajectory.center.y)
            }
        }
    }
}

interface MapElementProperties {
    val granularity: Int

    val elementSize: MapPixel get() = 1.grid2mpx / granularity

    val countInOneLine: Int get() = MAP_BLOCK_COUNT * granularity

    /** Bullet power has to be no less than this value to destroy the element */
    val strength: Int get() = 1
}

fun List<Int>.anyRealElements(indices: Set<MapElement>): Boolean =
    toSet().let { set -> indices.any { it.index in set } }