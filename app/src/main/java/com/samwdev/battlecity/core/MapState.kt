package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.MapElements
import com.samwdev.battlecity.entity.SteelElement

@Composable
fun rememberMapState(mapElements: MapElements): MapState {
    return remember(mapElements) { MapState(mapElements) }
}

class MapState(
    mapElements: MapElements,
) : TickListener {
    companion object {
        private const val FortificationDuration = 18 * 1000
        private const val FortificationTimeoutDuration = 3 * 1000

        private val DefaultPlayerSpawnPosition = Offset(4f.grid2mpx, 12f.grid2mpx)
        private val DefaultBotSpawnPositions = listOf(
            Offset(0f.grid2mpx, 0f.grid2mpx),
            Offset(6f.grid2mpx, 0f.grid2mpx),
            Offset(12f.grid2mpx, 0f.grid2mpx),
        )
    }
    // todo move to a proper place
    private var remainingFortificationTime: Int = 0

    val playerSpawnPosition = DefaultPlayerSpawnPosition
    val botSpawnPositions = DefaultBotSpawnPositions

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

    var accessPoints: AccessPoints by mutableStateOf(emptyAccessPoints())
        private set

    val iceIndexSet: Set<Int> = ices.map { it.index }.toSet()
    val brickIndexSet: Set<Int> get() = bricks.map { it.index }.toSet()
    val steelIndexSet: Set<Int> get() = steels.map { it.index }.toSet()
    val waterIndexSet: Set<Int> get() = waters.map { it.index }.toSet()

    init {
        updateAccessPoints(SubGrid(playerSpawnPosition), depth = Int.MAX_VALUE)
        botSpawnPositions.forEach {
            updateAccessPoints(SubGrid(it), depth = Int.MAX_VALUE)
        }
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
            remainingFortificationTime -= tick.delta
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
                val subGrid = BrickElement.getSubGrid(it)
                val cleared = !BrickElement.overlapsAnyElement(brickIndexSet, subGrid)
                if (cleared) {
                    // Only re-calc when an entire sub grid (a quarter block) is cleared. (a quarter block contains up to 4 brick elements)
                    // For performance purposes, use a depth of 10 for the calculation.
                    // It should do the job most of the time, unless we just unblocked a really deep dead end.
                    updateAccessPoints(subGrid, depth = 10)
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
                val subGrid = SteelElement.getSubGrid(it)
                // a destroyed steel always frees up a sub grid
                updateAccessPoints(subGrid, depth = 10)
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

    private fun updateAccessPoints(spreadFrom: SubGrid, depth: Int) {
        accessPoints = accessPoints.updated(waterIndexSet, steelIndexSet, brickIndexSet, spreadFrom, depth)
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
}