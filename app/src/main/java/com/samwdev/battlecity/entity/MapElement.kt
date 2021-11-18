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
}

data class BrickElement(override val index: Int) : MapElement(index), MapElementProperties by BrickElement {
    val patternIndex: Int get() = (gridPosition.x + gridPosition.y) % 2

    companion object : MapElementHelper(4) {
        operator fun invoke(row: Int, col: Int) = BrickElement(row * countInOneLine + col)
    }
}

data class SteelElement(override val index: Int) : MapElement(index), MapElementProperties by SteelElement {
    companion object : MapElementHelper(2) {
        override val strength: Int = 3
        operator fun invoke(row: Int, col: Int) = SteelElement(row * countInOneLine + col)
    }
}

data class TreeElement(override val index: Int) : MapElement(index), MapElementProperties by TreeElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int) = TreeElement(row * countInOneLine + col)
    }
}

data class WaterElement(override val index: Int) : MapElement(index), MapElementProperties by WaterElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int) = WaterElement(row * countInOneLine + col)
    }
}

data class IceElement(override val index: Int) : MapElement(index), MapElementProperties by IceElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int) = IceElement(row * countInOneLine + col)
    }
}

data class EagleElement(override val index: Int, val destroyed: Boolean = false) : MapElement(index), MapElementProperties by EagleElement {
    companion object : MapElementHelper(1) {
        operator fun invoke(row: Int, col: Int, destroyed: Boolean = false) = EagleElement(row * countInOneLine + col, destroyed)
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

    /**
     * This method returns the indices of the elements in the rect regardless of there actually is any elements in the rect.
     */
    fun getIndicesOverlappingRect(rect: Rect, moveDirection: Direction): List<Int> {
        val top: MapPixel = rect.top
        val right: MapPixel = rect.right
        val bottom: MapPixel = rect.bottom
        val left: MapPixel = rect.left

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
                    ret.add(r * countInOneLine + c)
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

    /**
     * Get the first actual hit point along the trajectory
     */
    fun getHitPoint(
        realElements: List<MapElement>,
        trajectory: Rect,
        direction: Direction,
    ): Offset? {
        val indicesAlongTrajectory = getIndicesOverlappingRect(trajectory, moveDirection = direction)
        val realSet = realElements.map { it.index }.toSet()

        // the first matching result is guaranteed one of the first hit elements in the direction
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

    @Deprecated("")
    fun getImpactedArea(
        realElements: List<MapElement>,
        trajectory: Rect,
        impactDepth: MapPixel,
        direction: Direction,
    ): Rect {
        val indicesAlongTrajectory = getIndicesOverlappingRect(trajectory, moveDirection = direction)
        val realSet = realElements.map { it.index }.toSet()

        val firstImpactedIndex = indicesAlongTrajectory.find { it in realSet } ?: return Rect.Zero
        val (leftMost, topMost, rightMost, bottomMost) = getRectByIndex(firstImpactedIndex)

        return when (direction) {
            Direction.Up -> {
                Rect(trajectory.left, bottomMost - impactDepth, trajectory.right, bottomMost)
            }
            Direction.Down -> {
                Rect(trajectory.left, topMost, trajectory.right, topMost + impactDepth)
            }
            Direction.Left -> {
                Rect(rightMost - impactDepth, trajectory.top, rightMost, trajectory.bottom)
            }
            Direction.Right -> {
                Rect(leftMost, trajectory.top, leftMost + impactDepth, trajectory.bottom)
            }
        }
    }

    @Deprecated("")
    fun getImpactedIndices(
        realElements: List<MapElement>,
        trajectory: Rect,
        impactDepth: MapPixel,
        direction: Direction,
    ): List<Int> {
        val impactedArea = getImpactedArea(realElements, trajectory, impactDepth, direction)
        if (impactedArea.isEmpty) {
            return emptyList()
        }
        return getIndicesOverlappingRect(impactedArea, direction)
    }
}

interface MapElementProperties {
    val granularity: Int

    val elementSize: MapPixel get() = 1.grid2mpx / granularity

    val countInOneLine: Int get() = MAP_BLOCK_COUNT * granularity

    /** Bullet power has to be no less than this value to destroy the element */
    val strength: Int get() = 1
}

fun List<Int>.anyRealElements(indices: List<MapElement>): Boolean =
    toSet().let { set -> indices.any { it.index in set } }