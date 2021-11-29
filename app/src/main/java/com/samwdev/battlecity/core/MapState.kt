package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.entity.SteelElement
import kotlin.math.max

@Composable
fun rememberMapState(stageConfig: StageConfig): MapState {
    return remember(stageConfig) { MapState(stageConfig) }
}

class MapState(
    stageConfig: StageConfig,
) : TickListener, GridUnitNumberAware {
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

    private val mapConfig = stageConfig.map

    val mapDifficulty: Int = stageConfig.difficulty // todo default to map config but should bump up after beating all maps
    override val hGridUnitNum: Int = mapConfig.hGridUnitNum
    override val vGridUnitNum: Int = mapConfig.vGridUnitNum

    val mapName: String by mutableStateOf(stageConfig.name)
    var remainingBot: Int by mutableStateOf(20) // todo factor in difficulty
        private set

    var remainingPlayerLife: Int by mutableStateOf(20)
        private set

    var bricks by mutableStateOf(mapConfig.bricks, policy = referentialEqualityPolicy())
        private set

    var steels by mutableStateOf(mapConfig.steels, policy = referentialEqualityPolicy())
        private set

    var trees by mutableStateOf(mapConfig.trees, policy = referentialEqualityPolicy())
        private set

    var waters by mutableStateOf(mapConfig.waters, policy = referentialEqualityPolicy())
        private set

    var ices by mutableStateOf(mapConfig.ices, policy = referentialEqualityPolicy())
        private set

    var eagle by mutableStateOf(mapConfig.eagle, policy = referentialEqualityPolicy())

    var accessPoints: AccessPoints by mutableStateOf(emptyAccessPoints(mapConfig.hGridUnitNum, mapConfig.vGridUnitNum))
        private set

    val iceIndexSet: Set<Int> = ices.map { it.index }.toSet()
    private val brickIndexSet: Set<Int> get() = bricks.map { it.index }.toSet()
    private val steelIndexSet: Set<Int> get() = steels.map { it.index }.toSet()
    private val waterIndexSet: Set<Int> get() = waters.map { it.index }.toSet()

    init {
        accessPoints = accessPoints.updated(waterIndexSet, steelIndexSet, brickIndexSet)
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
        val oldBrickIndex = brickIndexSet
        bricks = bricks.toMutableSet().apply {
            removeAll { it.index in indices }
        }
        val newBrickIndex = brickIndexSet
        val newCount = bricks.count()
        val destroyedSome = newCount != oldCount
        if (destroyedSome) {
            indices.asSequence().filter { it in oldBrickIndex }
                .map { BrickElement.getSubGrid(it) }
                .toSet() // remove dup
                .forEach { subGrid ->
                    val cleared = !BrickElement.overlapsAnyElement(newBrickIndex, subGrid)
                    if (cleared) {
                        // Only re-calc when an entire sub grid (a quarter block) is cleared. (a quarter block contains up to 4 brick elements)
                        // For performance purposes, use a depth of 10 for the calculation.
                        // It should do the job most of the time, unless we just unblocked a really deep dead end.
                        accessPoints = accessPoints.updated(
                            brickIndexSet = brickIndexSet,
                            steelIndexSet = steelIndexSet,
                            waterIndexSet = waterIndexSet,
                            spreadFrom = subGrid,
                            depth = 1
                        )
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
                accessPoints = accessPoints.updated(
                    brickIndexSet = brickIndexSet,
                    steelIndexSet = steelIndexSet,
                    waterIndexSet = waterIndexSet,
                    spreadFrom = subGrid,
                    depth = 1
                )
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

    // todo move to better place
    fun deductRemainingBot() {
        remainingBot = max(remainingBot - 1, 0)
    }

    // todo move to better place
    fun deductPlayerLife() {
        remainingPlayerLife -= 1
    }

    private fun wrapEagleWithSteels() {
        destroyBricksIndex(brickIndicesAroundEagle)
        steels = steels.toMutableSet()
            .apply { addAll(steelIndicesAroundEagle.map { SteelElement(it, hGridUnitNum) }) }
    }

    private fun wrapEagleWithBricks() {
        destroySteelsIndex(steelIndicesAroundEagle)
        bricks = bricks.toMutableSet()
            .apply { addAll(brickIndicesAroundEagle.map { BrickElement(it, hGridUnitNum) }) }
    }
}