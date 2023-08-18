package com.samwdev.battlecity.entity

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.SubGrid
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.ui.component.LocalGridSize

sealed class MapElement(open val index: Int, open val hGridSize: Int) : MapElementProperties {
    val gridPosition: IntOffset
        get() {
            val row = index / countInOneMapRow
            val col = index % countInOneMapRow
            return IntOffset(col, row)
        }

    val offsetInMapPixel: Offset get() {
        val (x, y) = gridPosition
        val rowF = y / granularity.toFloat()
        val colF = x / granularity.toFloat()
        return Offset(colF.cell2mpx, rowF.cell2mpx)
    }

    val countInOneMapRow: Int get() = hGridSize * granularity

    val rect: Rect get() = Rect(offset = offsetInMapPixel, size = Size(elementSize, elementSize))
}

data class BrickElement(
    override val index: Int,
    override val hGridSize: Int,
) : MapElement(index, hGridSize), MapElementProperties by BrickElement {
    val patternIndex: Int get() = (gridPosition.x + gridPosition.y) % 2

    companion object : MapElementHelper(4) {
        operator fun invoke(row: Int, col: Int, hGridSize: Int) =
            BrickElement(getIndex(row, col, hGridSize), hGridSize)

        @Composable
        fun compose(row: Int, col: Int): BrickElement {
            return invoke(row, col, hGridSize = LocalGridSize.current.first)
        }
    }
}

data class SteelElement(
    override val index: Int,
    override val hGridSize: Int,
) : MapElement(index, hGridSize), MapElementProperties by SteelElement {

    companion object : MapElementHelper(2) {
        override val strength: Int = 3
        operator fun invoke(row: Int, col: Int, hGridSize: Int) =
            SteelElement(getIndex(row, col, hGridSize), hGridSize)

        @Composable
        fun compose(row: Int, col: Int): SteelElement {
            return invoke(row, col, hGridSize = LocalGridSize.current.first)
        }
    }
}

data class TreeElement(
    override val index: Int,
    override val hGridSize: Int,
) : MapElement(index, hGridSize), MapElementProperties by TreeElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridSize: Int) =
            TreeElement(getIndex(row, col, hGridSize), hGridSize)

        @Composable
        fun compose(row: Int, col: Int): TreeElement {
            return invoke(row, col, hGridSize = LocalGridSize.current.first)
        }
    }
}

data class WaterElement(
    override val index: Int,
    override val hGridSize: Int,
) : MapElement(index, hGridSize), MapElementProperties by WaterElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridSize: Int) =
            WaterElement(getIndex(row, col, hGridSize), hGridSize)

        @Composable
        fun compose(row: Int, col: Int): WaterElement {
            return invoke(row, col, hGridSize = LocalGridSize.current.first)
        }
    }
}

data class IceElement(
    override val index: Int,
    override val hGridSize: Int,
) : MapElement(index, hGridSize), MapElementProperties by IceElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridSize: Int) =
            IceElement(getIndex(row, col, hGridSize), hGridSize)

        @Composable
        fun compose(row: Int, col: Int): IceElement {
            return invoke(row, col, hGridSize = LocalGridSize.current.first)
        }
    }
}

data class EagleElement(
    override val index: Int,
    override val hGridSize: Int,
    val dead: Boolean = false,
) : MapElement(index, hGridSize), MapElementProperties by EagleElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, hGridSize: Int, destroyed: Boolean = false) =
            EagleElement(getIndex(row, col, hGridSize), hGridSize, destroyed)

        @Composable
        fun compose(row: Int, col: Int, destroyed: Boolean = false): EagleElement {
            return invoke(row, col, hGridSize = LocalGridSize.current.first, destroyed = destroyed)
        }
    }
}

abstract class MapElementHelper(override val granularity: Int) : MapElementProperties {
    fun getRectByIndex(index: Int, hGridSize: Int): Rect {
        val (row, col) = getRowCol(index, hGridSize)
        return Rect(
            offset = Offset(col * elementSize, row * elementSize),
            size = Size(elementSize, elementSize),
        )
    }

    fun overlapsAnyElement(
        realElements: Set<Int>,
        subGrid: SubGrid,
        hGridSize: Int,
        hSpan: Int = 1,
        vSpan: Int = 1
    ): Boolean {
        require(hSpan in 1..2)
        require(vSpan in 1..2)
        // 2 is the sub granularity
        val elementCountInOneSubGridSide = granularity / 2f

        val rectToCheck = Rect(subGrid.topLeft, Size(hSpan * elementCountInOneSubGridSide * elementSize,
            vSpan * elementCountInOneSubGridSide * elementSize))
        val indices = getOverlapIndicesInRect(rectToCheck, hGridSize = hGridSize)
        return indices.any { it in realElements }
    }

    fun getIndex(row: Int, col: Int, hGridSize: Int) = row * hGridSize * granularity + col

    private fun getRowCol(index: Int, hGridSize: Int): Pair<Int, Int> =
        (hGridSize * granularity).let { countInOneRow -> index / countInOneRow to index % countInOneRow}

    fun getSubGrid(index: Int, hGridSize: Int): SubGrid {
        val (row, col) = getRowCol(index, hGridSize)
        val subRow = (row / (granularity / 2f)).toInt()
        val subCol = (col / (granularity / 2f)).toInt()
        return SubGrid(subRow, subCol)
    }

    /**
     * This method returns the indices of the elements that overlap with the rect
     * regardless if there actually is any elements in the rect.
     * @param moveDirection determines how the returned items are traversed. First hit items come first.
     */
    fun getOverlapIndicesInRect(rect: Rect, hGridSize: Int, moveDirection: Direction = Direction.Down): List<Int> {
        val top: MapPixel = rect.top
        val bottom: MapPixel = rect.bottom - 0.1f // exclude the bottom border
        val left: MapPixel = rect.left
        val right: MapPixel = rect.right - 0.1f // exclude the right border

        val col1 = (left / elementSize).toInt().coerceAtLeast(0)
        val row1 = (top / elementSize).toInt().coerceAtLeast(0)
        val col2 = (right / elementSize).toInt().coerceAtMost(hGridSize * granularity - 1)
        val row2 = (bottom / elementSize).toInt().coerceAtMost(hGridSize * granularity - 1)

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
                    ret.add(getIndex(r, c, hGridSize))
                }
            }
        } else {
            for (r in firstDimen) {
                for (c in secondDimen) {
                    ret.add(getIndex(r, c, hGridSize))
                }
            }
        }
        return ret
    }

    fun getImpactPoint(
        realElement: MapElement,
        trajectory: Rect,
        direction: Direction,
    ): Offset? = getImpactPoint(setOf(realElement), trajectory, direction)

    /**
     * Get the first actual hit point along the trajectory
     */
    fun getImpactPoint(
        realElements: Set<MapElement>,
        trajectory: Rect,
        direction: Direction,
    ): Offset? {
        if (realElements.isEmpty()) return null
        val vGridSize = realElements.first().hGridSize
        val indicesAlongTrajectory = getOverlapIndicesInRect(trajectory, hGridSize = vGridSize, moveDirection = direction)
        val realSet = realElements.map { it.index }.toSet()

        // The first matching element is guaranteed to be one of the first hit elements in the direction,
        // so we can use it to calculate the impact point's coordinate across the bullet's flying direction.
        // The other dimension of the coordinate will be the bullet's center along the flying direction.
        // E.g, for a bullet flying right, the "left" of the first hit elements is the x of the impact point,
        // the bullet center's y is the y of the impact point.
        val firstHitIndex = indicesAlongTrajectory.find { it in realSet } ?: return null
        val (leftMost, topMost, rightMost, bottomMost) = getRectByIndex(firstHitIndex, vGridSize)

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

    val elementSize: MapPixel get() = 1.cell2mpx / granularity

    /** Bullet power has to be no less than this value to destroy the element */
    val strength: Int get() = 1
}

fun List<Int>.anyRealElements(indices: Set<MapElement>): Boolean =
    toSet().let { set -> indices.any { it.index in set } }