package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.MapElements
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement

@Composable
fun rememberMapState(mapElements: MapElements): MapState {
    return remember(mapElements) { MapState(mapElements) }
}

typealias AccessPoints = Array<Array<Int>>

class MapState(
    mapElements: MapElements,
) : TickListener {
    companion object {
        private const val FortificationDuration = 18 * 1000
        private const val FortificationTimeoutDuration = 3 * 1000
        const val AccessPointsSize = MAP_BLOCK_COUNT * 2 - 1
    }
    // todo move to a proper place
    private var remainingFortificationTime: Int = 0

    val playerSpawnPosition = Offset(4f.grid2mpx, 12f.grid2mpx)

    var bricks by mutableStateOf(mapElements.bricks, policy = referentialEqualityPolicy())
        private set

    var steels by mutableStateOf(mapElements.steels, policy = referentialEqualityPolicy())
        private set

    var trees by mutableStateOf(mapElements.trees, policy = referentialEqualityPolicy())
        private set

    var waters by mutableStateOf(mapElements.waters, policy = referentialEqualityPolicy())
        private set

    var ices by mutableStateOf(mapElements.ices, policy = referentialEqualityPolicy())
        private set

    var eagle by mutableStateOf(mapElements.eagle, policy = referentialEqualityPolicy())

    var accessPoints: AccessPoints by mutableStateOf(Array(AccessPointsSize) { Array(AccessPointsSize) { -1 } })
        private set

    val iceIndexSet: Set<Int> = ices.map { it.index }.toSet()
    val brickIndexSet: Set<Int> get() = bricks.map { it.index }.toSet()
    val steelIndexSet: Set<Int> get() = steels.map { it.index }.toSet()
    val waterIndexSet: Set<Int> get() = waters.map { it.index }.toSet()

    init {
        calculateAccessPoints()
    }

    private val rectanglesAroundEagle = eagle.rect.let { eagleRect ->
        val top = Rect(
            offset = Offset(eagleRect.left - 0.5f.grid2mpx, eagleRect.top - 0.5f.grid2mpx),
            size = Size(2f.grid2mpx, 0.5f.grid2mpx)
        )
        val left = Rect(
            offset = Offset(eagleRect.left - 0.5f.grid2mpx, eagleRect.top),
            size = Size(0.5f.grid2mpx, 1f.grid2mpx)
        )
        val right = Rect(
            offset = Offset(eagleRect.right, eagleRect.top),
            size = Size(0.5f.grid2mpx, 1f.grid2mpx)
        )
        listOf(top, left, right)
    }

    private val brickIndicesAroundEagle = rectanglesAroundEagle.fold(mutableSetOf<Int>()) { acc, rect ->
        acc.apply { addAll(BrickElement.getIndicesOverlappingRect(rect)) }
    }

    private val steelIndicesAroundEagle = rectanglesAroundEagle.fold(mutableSetOf<Int>()) { acc, rect ->
        acc.apply { addAll(SteelElement.getIndicesOverlappingRect(rect)) }
    }

    override fun onTick(tick: Tick) {
        if (remainingFortificationTime > 0) {
            remainingFortificationTime -= tick.delta.toInt()
            if (remainingFortificationTime <= 0) {
                wrapEagleWithBricks()
            } else if (remainingFortificationTime < FortificationTimeoutDuration) {
                val blinkFrame = remainingFortificationTime / (FortificationTimeoutDuration / 12)
                if (blinkFrame % 2 == 0) {
                    wrapEagleWithSteels()
                } else {
                    wrapEagleWithBricks()
                }
            }
        }
    }

    fun destroyBricksIndex(indices: Set<Int>): Boolean {
        val oldCount = bricks.count()
        bricks = bricks.toMutableSet().apply {
            removeAll { it.index in indices }
        }
        val newCount = bricks.count()
        val destroyedSome = newCount != oldCount
        if (destroyedSome) {
            indices.forEach {
                val (subR, subC) = BrickElement.getSubRowCol(it)
                val cleared = !BrickElement.overlapsAnyElement(brickIndexSet, subR, subC)
                if (cleared) {
                    // Only re-calc when an entire sub block (quarter block) is cleared. (a quarter block contains up to 4 brick elements)
                    // For performance purposes, use a depth of 10 for the calculation.
                    // It should do the job most of the time, unless we just unblocked a really deep dead end.
                    calculateAccessPoints(subR, subC, depth = 10)
                }
            }
        }
        return destroyedSome
    }

    fun destroySteelsIndex(indices: Set<Int>): Boolean {
        val oldCount = steels.count()
        steels = steels.toMutableSet().apply {
            removeAll { it.index in indices }
        }
        val newCount = steels.count()
        val destroyedSome = newCount != oldCount
        if (destroyedSome) {
            indices.forEach {
                val (subR, subC) = SteelElement.getSubRowCol(it)
                // a destroyed steel always frees up a sub block
                calculateAccessPoints(subR, subC, depth = 10)
            }
        }
        return destroyedSome
    }

    fun fortifyBase() {
        remainingFortificationTime = FortificationDuration
        wrapEagleWithSteels()
    }

    fun destroyEagle() {
        eagle = eagle.copy(dead = true)
    }

    fun isGridAccessible(topLeft: Offset): Boolean {
        // todo
        return true
    }

    private fun wrapEagleWithSteels() {
        destroyBricksIndex(brickIndicesAroundEagle)
        steels = steels.toMutableSet()
            .apply { addAll(steelIndicesAroundEagle.map { SteelElement(it) }) }
    }

    private fun wrapEagleWithBricks() {
        destroySteelsIndex(steelIndicesAroundEagle)
        bricks = bricks.toMutableSet()
            .apply { addAll(brickIndicesAroundEagle.map { BrickElement(it) }) }
    }

    private fun calculateAccessPoints(
        fromSubRow: Int = (playerSpawnPosition.y / 1f.grid2mpx * 2).toInt(),
        fromSubCol: Int = (playerSpawnPosition.x / 1f.grid2mpx * 2).toInt(),
        depth: Int = Int.MAX_VALUE,
    ) {
        val accPts = accessPoints.copyOf()
        calculateAccessPointsRecursively(accPts, fromSubRow, fromSubCol, depth)
        accessPoints = accPts
    }

    private fun calculateAccessPointsRecursively(accessPoints: AccessPoints, row: Int, col: Int, depth: Int) {
        if (depth < 0 || row !in accessPoints.indices || col !in accessPoints.first().indices) return
        if (accessPoints[row][col] > 0) return // already accessed

        // starting from the cheapest
        if (WaterElement.overlapsAnyElement(waterIndexSet, row, col) ||
            SteelElement.overlapsAnyElement(steelIndexSet, row, col) ||
            BrickElement.overlapsAnyElement(brickIndexSet, row, col)
        ) {
            accessPoints[row][col] = -1
            return
        } else {
            accessPoints[row][col] = 1
            calculateAccessPointsRecursively(accessPoints, row - 1, col, depth - 1)
            calculateAccessPointsRecursively(accessPoints, row + 1, col, depth - 1)
            calculateAccessPointsRecursively(accessPoints, row, col - 1, depth - 1)
            calculateAccessPointsRecursively(accessPoints, row, col + 1, depth - 1)
        }
    }
}