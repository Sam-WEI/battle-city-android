package com.samwdev.battlecity.core

import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement
import kotlin.random.Random

private const val ValueUninitialized = 0
private const val ValueAccessible = 1
private const val ValueObstacleSteel = -10000
private const val ValueObstacleBrick = -100
private const val ValueObstacleWater = -10
private const val ValueEagleArea = -2
/**
 * When a subGrid itself is accessible but its right or bottom neighbor(s) is not,
 * this subGrid is still inaccessible because it can't fit a tank. Use this value to indicate that.
 */
private const val ValueNeighborIsObstacle = -5

typealias AccessPoints = Array<Array<Int>>

fun emptyAccessPoints(hGridUnitNum: Int, vGridUnitNum: Int) =
    Array(vGridUnitNum * 2) { Array(hGridUnitNum * 2) { ValueUninitialized } }

val AccessPoints.hSubGridUnitNum: Int get() = this[0].size
val AccessPoints.vSubGridUnitNum: Int get() = size

val AccessPoints.bottomRight: SubGrid get() = SubGrid(vSubGridUnitNum - 1, hSubGridUnitNum - 1)

/**
 * Return an updated copy
 * @param hardRefresh pass true if new obstacles added, i.e, base fortification.
 */
fun AccessPoints.updated(
    waterIndexSet: Set<Int>,
    steelIndexSet: Set<Int>,
    brickIndexSet: Set<Int>,
    eagleAreaSubGrids: Set<SubGrid>,
    spreadFrom: SubGrid? = null,
    depth: Int = Int.MAX_VALUE,
    hardRefresh: Boolean = false,
): AccessPoints {
    val updated = copyOf()
    updated.updateInPlace(
        waterIndexSet,
        steelIndexSet,
        brickIndexSet,
        eagleAreaSubGrids,
        spreadFrom ?: bottomRight,
        depth,
        hardRefresh,
    )
    return updated
}

fun AccessPoints.randomAccessiblePoint(maxAttempt: Int = 5): SubGrid {
    var attempt = 0
    while (true) {
        val row = Random.nextInt(vSubGridUnitNum)
        val col = Random.nextInt(hSubGridUnitNum)
        if (this[row][col] > 0 || ++attempt >= maxAttempt) {
            return SubGrid(row, col)
        }
    }
}

operator fun AccessPoints.get(subGrid: SubGrid): Int = this[subGrid.subRow][subGrid.subCol]
operator fun AccessPoints.set(subGrid: SubGrid, value: Int) {
    this[subGrid.subRow][subGrid.subCol] = value
}

fun AccessPoints.isAccessible(subGrid: SubGrid): Boolean = this[subGrid] > 0
fun AccessPoints.isEagleArea(subGrid: SubGrid): Boolean = this[subGrid] == ValueEagleArea
fun AccessPoints.isBrick(subGrid: SubGrid): Boolean = this[subGrid] == ValueObstacleBrick

/**
 * @param hardRefresh pass true to also invalidate already accessible ones
 */
private fun AccessPoints.updateInPlace(
    waterIndexSet: Set<Int>,
    steelIndexSet: Set<Int>,
    brickIndexSet: Set<Int>,
    eagleAreaSubGrids: Set<SubGrid>,
    spreadFrom: SubGrid,
    depth: Int = Int.MAX_VALUE,
    hardRefresh: Boolean = false,
) {
    val (rowB, colB) = spreadFrom
    val top = (rowB - depth).coerceAtLeast(0)
    val left = (colB - depth).coerceAtLeast(0)
    for (row in rowB downTo top) {
        for (col in colB downTo left) {
            val curr = SubGrid(row, col)
            if (!hardRefresh && isAccessible(curr)) {
                continue
            }
            val value = when {
                curr in eagleAreaSubGrids -> ValueEagleArea
                WaterElement.overlapsAnyElement(
                    waterIndexSet,
                    curr,
                    hGridUnitNum = hSubGridUnitNum / 2,
                    1,
                    1
                ) -> ValueObstacleWater
                SteelElement.overlapsAnyElement(
                    steelIndexSet,
                    curr,
                    hGridUnitNum = hSubGridUnitNum / 2,
                    1,
                    1
                ) -> ValueObstacleSteel
                BrickElement.overlapsAnyElement(
                    brickIndexSet,
                    curr,
                    hGridUnitNum = hSubGridUnitNum / 2,
                    1,
                    1
                ) -> ValueObstacleBrick
                else -> ValueAccessible
            }
            this[curr] = value
            if (value == ValueAccessible) {
                // dynamic programming
                if (
                    curr.neighborRight.isOutOfBound(this)
                    || curr.neighborDown.isOutOfBound(this)
                    || this[curr.neighborDown] < ValueNeighborIsObstacle
                    || this[curr.neighborRight] < ValueNeighborIsObstacle
                    || this[curr.neighborRight.neighborDown] < ValueNeighborIsObstacle
                ) {
                    this[curr] = ValueNeighborIsObstacle
                }
            }
        }
    }
}

inline class SubGrid internal constructor(private val packedValue: Int) : Comparable<SubGrid> {
    val subRow: Int get() = packedValue / 10000
    val subCol: Int get() = packedValue % 10000

    val x: MapPixel get() = (subCol / 2f).grid2mpx
    val y: MapPixel get() = (subRow / 2f).grid2mpx

    val topLeft: Offset get() = Offset(x, y)

    fun isOutOfBound(accessPoints: AccessPoints): Boolean =
        !(subRow in 0 until accessPoints.vSubGridUnitNum && subCol in 0 until accessPoints.hSubGridUnitNum)

    val neighborUp: SubGrid get() = this - SubGrid(1, 0)
    val neighborDown: SubGrid get() = this + SubGrid(1, 0)
    val neighborLeft: SubGrid get() = this - SubGrid(0, 1)
    val neighborRight: SubGrid get() = this + SubGrid(0, 1)

    fun getNeighborInDirection(direction: Direction): SubGrid = when (direction) {
        Direction.Up -> neighborUp
        Direction.Down -> neighborDown
        Direction.Left -> neighborLeft
        Direction.Right -> neighborRight
    }

    operator fun component1(): Int = subRow
    operator fun component2(): Int = subCol
    operator fun plus(other: SubGrid) = SubGrid(subRow + other.subRow, subCol + other.subCol)
    operator fun minus(other: SubGrid) = SubGrid(subRow - other.subRow, subCol - other.subCol)

    override fun compareTo(other: SubGrid): Int = packedValue - other.packedValue

    override fun toString(): String {
        return "SubGrid[subRow=$subRow][subCol=$subCol]"
    }
}

// todo use bit
fun SubGrid(subRow: Int, subCol: Int): SubGrid = SubGrid(subRow * 10000 + subCol)

fun SubGrid(offset: Offset): SubGrid = SubGrid(
    (offset.y / 1f.grid2mpx).toInt() * 2, (offset.x / 1f.grid2mpx).toInt() * 2)

val Offset.subGrid: SubGrid get() {
    val subRow = (y / 0.5f.grid2mpx).toInt()
    val subCol = (x / 0.5f.grid2mpx).toInt()
    return SubGrid(subRow, subCol)
}