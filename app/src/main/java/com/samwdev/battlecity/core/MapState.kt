package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.MapDifficulty
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.entity.SteelElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max

class MapState(stageConfig: StageConfig) : TickListener, GridSizeAware {
    companion object {
        private const val FortificationDuration = 18 * 1000
        private const val FortificationBlinkDuration = 3 * 1000

        private val DefaultPlayerSpawnPosition = Offset(4f.cell2mpx, 12f.cell2mpx)
        private val DefaultBotSpawnPositions = listOf(
            Offset(0f.cell2mpx, 0f.cell2mpx),
            Offset(6f.cell2mpx, 0f.cell2mpx),
            Offset(12f.cell2mpx, 0f.cell2mpx),
        )
    }
    private val _inGameEventFlow = MutableStateFlow<GameStatus>(Playing)
    val inGameEventFlow: StateFlow<GameStatus> = _inGameEventFlow

    private var remainingFortificationTime: Int = 0
    private val mapConfig = stageConfig.map
    val botGroups = stageConfig.bots
    val mapDifficulty: MapDifficulty = stageConfig.difficulty // todo default to map config but should bump up after beating all maps

    val playerSpawnPosition = DefaultPlayerSpawnPosition
    val botSpawnPositions = DefaultBotSpawnPositions

    override val hGridSize: Int = mapConfig.hGridSize
    override val vGridSize: Int = mapConfig.vGridSize

    val mapName: String by mutableStateOf(stageConfig.name)

    var remainingBot: Int by mutableIntStateOf(20) // todo factor in difficulty
        private set

    var remainingPlayerLife: Int by mutableIntStateOf(3)
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

    var accessPoints: AccessPoints by mutableStateOf(emptyAccessPoints(mapConfig.hGridSize, mapConfig.vGridSize))
        private set

    val iceIndexSet: Set<Int> = ices.map { it.index }.toSet()
    private val brickIndexSet: Set<Int> get() = bricks.map { it.index }.toSet()
    private val steelIndexSet: Set<Int> get() = steels.map { it.index }.toSet()
    private val waterIndexSet: Set<Int> get() = waters.map { it.index }.toSet()

    val subGridsOfEagleArea: Set<SubGrid> by lazy {
        // use Steel to do the calculation since one steel is one subGrid
        val steelIndicesOfEagleArea = SteelElement.getOverlapIndicesInRect(eagle.rect.inflate(SteelElement.elementSize + 1f), hGridSize)
        steelIndicesOfEagleArea.map { idx -> SteelElement.getSubGrid(idx, hGridSize) }.toSet()
    }

    init {
        refreshAccessPoints()
    }

    private val rectanglesAroundEagle = eagle.rect.let { eagleRect ->
        val top = Rect(
            offset = Offset(eagleRect.left - 0.5f.cell2mpx, eagleRect.top - 0.5f.cell2mpx),
            size = Size(2f.cell2mpx, 0.5f.cell2mpx)
        )
        val left = Rect(
            offset = Offset(eagleRect.left - 0.5f.cell2mpx, eagleRect.top),
            size = Size(0.5f.cell2mpx, 1f.cell2mpx)
        )
        val right = Rect(
            offset = Offset(eagleRect.right, eagleRect.top),
            size = Size(0.5f.cell2mpx, 1f.cell2mpx)
        )
        listOf(top, left, right)
    }

    private val brickIndicesAroundEagle = rectanglesAroundEagle.fold(mutableSetOf<Int>()) { acc, rect ->
        acc.apply { addAll(BrickElement.getOverlapIndicesInRect(rect, hGridSize)) }
    }

    private val steelIndicesAroundEagle = rectanglesAroundEagle.fold(mutableSetOf<Int>()) { acc, rect ->
        acc.apply { addAll(SteelElement.getOverlapIndicesInRect(rect, hGridSize)) }
    }

    private val bottomRightSubGridAroundEagle: SubGrid =
        rectanglesAroundEagle.asSequence().map { it.topLeft }.maxOf { it.subGrid }

    private val subGridDepthAroundEagle: Int get() {
        val topLeft = rectanglesAroundEagle.asSequence().map { it.topLeft }.minOf { it.subGrid }
        return max(bottomRightSubGridAroundEagle.subRow - topLeft.subRow, bottomRightSubGridAroundEagle.subCol - topLeft.subCol) + 1
    }

    override fun onTick(tick: Tick) {
        if (remainingFortificationTime > 0) {
            remainingFortificationTime -= tick.delta
            if (remainingFortificationTime <= 0) {
                wrapEagleWithBricks()
            } else if (remainingFortificationTime < FortificationBlinkDuration) {
                val blinkFrame = remainingFortificationTime / (FortificationBlinkDuration / 12)
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
                        refreshAccessPoints(subGrid, 1)
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
                val subGrid = SteelElement.getSubGrid(it, hGridSize)
                // a destroyed steel always frees up a sub grid
                refreshAccessPoints(subGrid, 1)
            }
        }
        return destroyedSome
    }

    fun fortifyBase(duration: Int = FortificationDuration) {
        remainingFortificationTime = duration
        wrapEagleWithSteels()
    }

    fun destroyEagle() {
        eagle = eagle.copy(dead = true)
        // todo use a StateFlow
        _inGameEventFlow.value = GameOver
    }

    fun deductRemainingBot() {
        remainingBot = max(remainingBot - 1, 0)
    }

    fun addPlayerLife() {
        remainingPlayerLife += 1
    }

    fun deductPlayerLife(): Boolean {
        if (remainingPlayerLife == 0) {
            _inGameEventFlow.value = GameOver
            return false
        }
        remainingPlayerLife -= 1
        return true
    }

    fun mapClear() {
        _inGameEventFlow.value = MapCleared
    }

    /**
     * To refresh all access points, use default values.
     * @param spreadFrom pass null to refresh from bottom right
     * @param depth pass [Int.MAX_VALUE] for max depth
     * @param hardRefresh pass true if new obstacles added, i.e, base fortification.
     */
    private fun refreshAccessPoints(spreadFrom: SubGrid? = null, depth: Int = Int.MAX_VALUE, hardRefresh: Boolean = false) {
        accessPoints = accessPoints.updated(
            brickIndexSet = brickIndexSet,
            steelIndexSet = steelIndexSet,
            waterIndexSet = waterIndexSet,
            eagleAreaSubGrids = subGridsOfEagleArea,
            spreadFrom = spreadFrom,
            depth = depth,
            hardRefresh = hardRefresh,
        )
    }

    private fun refreshAccessPointsAroundEagle() {
        refreshAccessPoints(
            spreadFrom = bottomRightSubGridAroundEagle,
            depth = subGridDepthAroundEagle,
            hardRefresh = true,
        )
    }

    private fun wrapEagleWithSteels() {
        destroyBricksIndex(brickIndicesAroundEagle)
        steels = steels.toMutableSet()
            .apply { addAll(steelIndicesAroundEagle.map { SteelElement(it, hGridSize) }) }
        refreshAccessPointsAroundEagle()
    }

    private fun wrapEagleWithBricks() {
        destroySteelsIndex(steelIndicesAroundEagle)
        bricks = bricks.toMutableSet()
            .apply { addAll(brickIndicesAroundEagle.map { BrickElement(it, hGridSize) }) }
        refreshAccessPointsAroundEagle()
    }
}