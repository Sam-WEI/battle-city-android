package com.samwdev.battlecity.core

import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement
import kotlin.random.Random

private const val AccessPointsSizeInternal = MAP_BLOCK_COUNT * 2
private const val AccessPointsSize = MAP_BLOCK_COUNT * 2
private const val ValueUninitialized = 0
private const val ValueAccessible = 1
private const val ValueObstacleSteel = -10000
private const val ValueObstacleBrick = -100
private const val ValueObstacleWater = -10
private const val ValueNeighborObstacle = -1

typealias AccessPoints = Array<Array<Int>>

fun emptyAccessPoints() = Array(AccessPointsSizeInternal) { Array(AccessPointsSizeInternal) { ValueUninitialized } }

/** Return an updated copy */
fun AccessPoints.updated(
    waterIndexSet: Set<Int>,
    steelIndexSet: Set<Int>,
    brickIndexSet: Set<Int>,
    spreadFrom: SubGrid = SubGrid(AccessPointsSize - 1, AccessPointsSize - 1),
    depth: Int = Int.MAX_VALUE
): AccessPoints {
    val updated = copyOf()
    updated.calculateInPlace(waterIndexSet, steelIndexSet, brickIndexSet, spreadFrom, depth)
    return updated
}

fun AccessPoints.randomAccessiblePoint(maxAttempt: Int = 5): SubGrid {
    var attempt = 0
    while (true) {
        val row = Random.nextInt(AccessPointsSize)
        val col = Random.nextInt(AccessPointsSize)
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

private fun AccessPoints.calculateInPlace(
    waterIndexSet: Set<Int>,
    steelIndexSet: Set<Int>,
    brickIndexSet: Set<Int>,
    spreadFrom: SubGrid,
    depth: Int = Int.MAX_VALUE
) {
    val (rowB, colB) = spreadFrom
    val top = (rowB - depth).coerceAtLeast(0)
    val left = (colB - depth).coerceAtLeast(0)
    for (row in rowB downTo top) {
        for (col in colB downTo left) {
            val curr = SubGrid(row, col)
            if (isAccessible(curr)) {
                continue
            }
            val value = when {
                WaterElement.overlapsAnyElement(waterIndexSet, curr, 1, 1) -> ValueObstacleWater
                SteelElement.overlapsAnyElement(steelIndexSet, curr, 1, 1) -> ValueObstacleSteel
                BrickElement.overlapsAnyElement(brickIndexSet, curr, 1, 1) -> ValueObstacleBrick
                else -> ValueAccessible
            }
            this[curr] = value
            if (value == ValueAccessible) {
                if (
                    curr.neighborRight.isOutOfBound
                    || curr.neighborDown.isOutOfBound
                    || this[curr.neighborDown] < ValueNeighborObstacle
                    || this[curr.neighborRight] < ValueNeighborObstacle
                    || this[curr.neighborRight.neighborDown] < ValueNeighborObstacle
                ) {
                    this[curr] = ValueNeighborObstacle
                }
            }
        }
    }
}

inline class SubGrid internal constructor(private val packedValue: Int) {
    val subRow: Int get() = packedValue / 10000
    val subCol: Int get() = packedValue % 10000

    val x: MapPixel get() = (subCol / 2f).grid2mpx
    val y: MapPixel get() = (subRow / 2f).grid2mpx

    val topLeft: Offset get() = Offset(x, y)

    val isOutOfBound: Boolean get() = !(subRow in 0 until AccessPointsSize && subCol in 0 until AccessPointsSize)

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

    override fun toString(): String {
        return "SubGrid[subRow=$subRow][subCol=$subCol]"
    }
}

fun SubGrid(subRow: Int, subCol: Int): SubGrid = SubGrid(subRow * 10000 + subCol)

fun SubGrid(offset: Offset): SubGrid = SubGrid(
    (offset.y / 1f.grid2mpx).toInt() * 2, (offset.x / 1f.grid2mpx).toInt() * 2)

val Offset.subGrid: SubGrid get() {
    val subRow = (y / 0.5f.grid2mpx).toInt()
    val subCol = (x / 0.5f.grid2mpx).toInt()
    return SubGrid(subRow, subCol)
}