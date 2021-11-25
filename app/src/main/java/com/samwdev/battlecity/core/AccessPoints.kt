package com.samwdev.battlecity.core

import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement
import kotlin.random.Random

private const val AccessPointsSize = MAP_BLOCK_COUNT * 2 - 1

typealias AccessPoints = Array<Array<Int>>

fun emptyAccessPoints() = Array(AccessPointsSize) { Array(AccessPointsSize) { -1 } }

/** Return an updated copy */
fun AccessPoints.updated(
    waterIndexSet: Set<Int>,
    steelIndexSet: Set<Int>,
    brickIndexSet: Set<Int>,
    spreadFrom: SubGrid,
    depth: Int = Int.MAX_VALUE
): AccessPoints {
    val updated = copyOf()
    calculateAccessPointsRecursive(waterIndexSet, steelIndexSet, brickIndexSet, updated, spreadFrom, depth)
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

operator fun AccessPoints.get(subGrid: SubGrid) = this[subGrid.subRow][subGrid.subCol]
operator fun AccessPoints.set(subGrid: SubGrid, value: Int) {
    this[subGrid.subRow][subGrid.subCol] = value
}

private fun calculateAccessPointsRecursive(
    waterIndexSet: Set<Int>,
    steelIndexSet: Set<Int>,
    brickIndexSet: Set<Int>,
    accessPoints: AccessPoints,
    spreadFrom: SubGrid,
    depth: Int
) {
    if (depth < 0 || spreadFrom.isOutOfBound) return
    if (accessPoints[spreadFrom] > 0) return // already accessed

    // starting from the cheapest
    if (WaterElement.overlapsAnyElement(waterIndexSet, spreadFrom) ||
        SteelElement.overlapsAnyElement(steelIndexSet, spreadFrom) ||
        BrickElement.overlapsAnyElement(brickIndexSet, spreadFrom)
    ) {
        accessPoints[spreadFrom] = -1
        return
    } else {
        accessPoints[spreadFrom] = 1
        calculateAccessPointsRecursive(waterIndexSet, steelIndexSet, brickIndexSet, accessPoints, spreadFrom - SubGrid(1, 0), depth - 1)
        calculateAccessPointsRecursive(waterIndexSet, steelIndexSet, brickIndexSet, accessPoints, spreadFrom + SubGrid(1, 0), depth - 1)
        calculateAccessPointsRecursive(waterIndexSet, steelIndexSet, brickIndexSet, accessPoints, spreadFrom - SubGrid(0, 1), depth - 1)
        calculateAccessPointsRecursive(waterIndexSet, steelIndexSet, brickIndexSet, accessPoints, spreadFrom + SubGrid(0, 1), depth - 1)
    }
}

@JvmInline
value class SubGrid internal constructor(private val packedValue: Int) {
    val subRow: Int get() = packedValue / AccessPointsSize
    val subCol: Int get() = packedValue % AccessPointsSize

    val x: MapPixel get() = (subRow / 2f).grid2mpx
    val y: MapPixel get() = (subCol / 2f).grid2mpx

    val isOutOfBound: Boolean get() = !(subRow in 0 until AccessPointsSize && subCol in 0 until AccessPointsSize)

    operator fun component1(): Int = subRow
    operator fun component2(): Int = subCol
    operator fun plus(other: SubGrid) = SubGrid(subRow + other.subRow, subCol + other.subCol)
    operator fun minus(other: SubGrid) = SubGrid(subRow - other.subRow, subCol - other.subCol)
}

fun SubGrid(subRow: Int, subCol: Int): SubGrid = SubGrid(subRow * AccessPointsSize + subCol)

fun SubGrid(offset: Offset): SubGrid = SubGrid(
    (offset.y / 1f.grid2mpx).toInt() * 2, (offset.x / 1f.grid2mpx).toInt() * 2)

val Offset.subGrid: SubGrid get() {
    val subRow = (y / 0.5f.grid2mpx).toInt()
    val subCol = (x / 0.5f.grid2mpx).toInt()
    return SubGrid(subRow, subCol)
}