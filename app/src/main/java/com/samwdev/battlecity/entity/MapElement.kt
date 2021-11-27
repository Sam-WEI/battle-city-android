package com.samwdev.battlecity.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.core.*

sealed class MapElement(open val index: Int, open val hGridUnitNum: Int) : MapElementProperties {
    val gridPosition: IntOffset
        get() {
            val row = index / countInOneRow
            val col = index % countInOneRow
            return IntOffset(col, row)
        }

    val offsetInMapPixel: Offset get() {
        val (x, y) = gridPosition
        val rowF = y / granularity.toFloat()
        val colF = x / granularity.toFloat()
        return Offset(colF.grid2mpx, rowF.grid2mpx)
    }

    val countInOneRow: Int get() = hGridUnitNum * granularity

    val rect: Rect get() = Rect(offset = offsetInMapPixel, size = Size(elementSize, elementSize))
}

data class BrickElement(
    override val index: Int,
    override val hGridUnitNum: Int,
) : MapElement(index, hGridUnitNum), MapElementProperties by BrickElement {
    val patternIndex: Int get() = (gridPosition.x + gridPosition.y) % 2

    companion object : MapElementHelper(4) {
        operator fun invoke(row: Int, col: Int, hGridUnitNum: Int) =
            BrickElement(getIndex(row, col, hGridUnitNum), hGridUnitNum)
    }
}

data class SteelElement(
    override val index: Int,
    override val hGridUnitNum: Int,
) : MapElement(index, hGridUnitNum), MapElementProperties by SteelElement {

    companion object : MapElementHelper(2) {
        override val strength: Int = 3
        operator fun invoke(row: Int, col: Int, hGridUnitNum: Int) =
            SteelElement(getIndex(row, col, hGridUnitNum), hGridUnitNum)
    }
}

data class TreeElement(
    override val index: Int,
    override val hGridUnitNum: Int,
) : MapElement(index, hGridUnitNum), MapElementProperties by TreeElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridUnitNum: Int) =
            TreeElement(getIndex(row, col, hGridUnitNum), hGridUnitNum)
    }
}

data class WaterElement(
    override val index: Int,
    override val hGridUnitNum: Int,
) : MapElement(index, hGridUnitNum), MapElementProperties by WaterElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridUnitNum: Int) =
            WaterElement(getIndex(row, col, hGridUnitNum), hGridUnitNum)
    }
}

data class IceElement(
    override val index: Int,
    override val hGridUnitNum: Int,
) : MapElement(index, hGridUnitNum), MapElementProperties by IceElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridUnitNum: Int) =
            IceElement(getIndex(row, col, hGridUnitNum), hGridUnitNum)
    }
}

data class EagleElement(
    override val index: Int,
    override val hGridUnitNum: Int,
    val dead: Boolean = false,
) : MapElement(index, hGridUnitNum), MapElementProperties by EagleElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridUnitNum: Int, destroyed: Boolean = false) =
            EagleElement(getIndex(row, col, hGridUnitNum), hGridUnitNum, destroyed)
    }
}

abstract class MapElementHelper(override val granularity: Int) : MapElementProperties {
    fun getRectByIndex(index: Int, hGridUnitNum: Int): Rect {
        val (row, col) = getRowCol(index, hGridUnitNum)
        return Rect(
            offset = Offset(col * elementSize, row * elementSize),
            size = Size(elementSize, elementSize),
        )
    }

    fun overlapsAnyElement(realElements: Set<Int>, subGrid: SubGrid, hSpan: Int = 1, vSpan: Int = 1, hGridUnitNum: Int): Boolean {
        require(hSpan > 0 && vSpan <= 2)
        require(vSpan > 0 && hSpan <= 2)
        // 2 is the sub granularity
        val elementCountInOneSubGridSide = granularity / 2f

        val rectToCheck = Rect(subGrid.topLeft, Size(hSpan * elementCountInOneSubGridSide * elementSize,
            vSpan * elementCountInOneSubGridSide * elementSize))
        val indices = getIndicesOverlappingRect(rectToCheck, hGridUnitNum = hGridUnitNum)
        return indices.any { it in realElements }
    }

    fun getIndex(row: Int, col: Int, hGridUnitNum: Int) = row * hGridUnitNum * granularity + col

    private fun getRowCol(index: Int, hGridUnitNum: Int): Pair<Int, Int> =
        (hGridUnitNum * granularity).let { countInOneRow -> index / countInOneRow to index % countInOneRow}

    fun getSubGrid(index: Int, hGridUnitNum: Int): SubGrid {
        val (row, col) = getRowCol(index, hGridUnitNum)
        val subRow = (row / (granularity / 2f)).toInt()
        val subCol = (col / (granularity / 2f)).toInt()
        return SubGrid(subRow, subCol)
    }

    /**
     * This method returns the indices of the elements in the rect regardless of there actually is any elements in the rect.
     * @param moveDirection determines how the returned items are traversed. First hit items come first.
     */
    fun getIndicesOverlappingRect(rect: Rect, hGridUnitNum: Int, moveDirection: Direction = Direction.Down): List<Int> {
        val top: MapPixel = rect.top
        val bottom: MapPixel = rect.bottom - 0.1f // exclude the bottom border
        val left: MapPixel = rect.left
        val right: MapPixel = rect.right - 0.1f // exclude the right border

        // todo! confirm working
        val col1 = (left / elementSize).toInt()//.coerceAtLeast(0)
        val row1 = (top / elementSize).toInt()//.coerceAtLeast(0)
        val col2 = (right / elementSize).toInt()//.coerceAtMost(countInOneLine - 1)
        val row2 = (bottom / elementSize).toInt()//.coerceAtMost(countInOneLine - 1)

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
        if (moveDirection.isHorizontal) {
            for (c in firstDimen) {
                for (r in secondDimen) {
                    ret.add(getIndex(r, c, hGridUnitNum))
                }
            }
        } else {
            for (r in firstDimen) {
                for (c in secondDimen) {
                    ret.add(getIndex(r, c, hGridUnitNum))
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
        if (realElements.isEmpty()) return null
        val vGridUnitNum = realElements.first().hGridUnitNum
        val indicesAlongTrajectory = getIndicesOverlappingRect(trajectory, hGridUnitNum = vGridUnitNum, moveDirection = direction)
        val realSet = realElements.map { it.index }.toSet()

        // the first matching element is guaranteed to be one of the first hit elements in the direction,
        // so we can use it to get the hit point's dimension across the bullet flying direction,
        // the other dimension will be the bullet's center.
        // e.g, for a bullet flying right, the "left" of the first hit elements shares the x of the hit point,
        // the bullet center's y is the y of the hit point.
        val firstHitIndex = indicesAlongTrajectory.find { it in realSet } ?: return null
        val (leftMost, topMost, rightMost, bottomMost) = getRectByIndex(firstHitIndex, vGridUnitNum)

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
    /** element count in one grid unit, e.g, 4 for brick, 2 for steel. */
    val granularity: Int

    val elementSize: MapPixel get() = 1.grid2mpx / granularity

    /** Bullet power has to be no less than this value to destroy the element */
    val strength: Int get() = 1
}

fun List<Int>.anyRealElements(indices: Set<MapElement>): Boolean =
    toSet().let { set -> indices.any { it.index in set } }