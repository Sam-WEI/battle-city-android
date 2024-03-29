package com.samwdev.battlecity.core.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.core.AccessPoints
import com.samwdev.battlecity.core.BattleResult
import com.samwdev.battlecity.core.GridSizeAware
import com.samwdev.battlecity.core.SubGrid
import com.samwdev.battlecity.core.TickListener
import com.samwdev.battlecity.core.Timer
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.core.emptyAccessPoints
import com.samwdev.battlecity.core.subGrid
import com.samwdev.battlecity.core.updated
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.MapDifficulty
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.entity.SteelElement
import kotlin.math.max

/**
 * Keeps track of map states such as remaining bricks, steels, access points and base fortification
 */
class MapState(
    private val gameState: GameState,
    stageConfig: StageConfig,
) : TickListener(), GridSizeAware {
    companion object {
        private const val FortificationDuration = 18 * 1000
        private const val FortificationBlinkDuration = 3 * 1000
        private const val FrozenDuration = 10 * 1000

        private val DefaultPlayerSpawnPosition = Offset(4f.cell2mpx, 12f.cell2mpx)
        private val DefaultBotSpawnPositions = listOf(
            Offset(0f.cell2mpx, 0f.cell2mpx),
            Offset(6f.cell2mpx, 0f.cell2mpx),
            Offset(12f.cell2mpx, 0f.cell2mpx),
        )
    }

    private var remainingFortificationTimer: Timer = Timer()
    private val mapConfig = stageConfig.map

    val mapName: String by mutableStateOf(stageConfig.name)
    val botGroups = stageConfig.bots
    val mapDifficulty: MapDifficulty = stageConfig.difficulty // todo default to map config but should bump up after beating all maps

    val playerSpawnPosition = DefaultPlayerSpawnPosition
    val botSpawnPositions = DefaultBotSpawnPositions

    private var botsFrozenTimer: Timer = Timer(FrozenDuration)

    val areBotsFrozen: Boolean get() = botsFrozenTimer.isActive

    override val hGridSize: Int = mapConfig.hGridSize
    override val vGridSize: Int = mapConfig.vGridSize

    var remainingBot: Int by mutableIntStateOf(20) // todo factor in difficulty
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
        if (remainingFortificationTimer.isActive) {
            if (remainingFortificationTimer.tick(tick)) {
                wrapEagleWithBricks()
            } else {
                if (remainingFortificationTimer.remainingTime < FortificationBlinkDuration) {
                    val blinkFrame = remainingFortificationTimer.remainingTime / (FortificationBlinkDuration / 12) // blink 12 times
                    if (blinkFrame % 2 == 0) {
                        wrapEagleWithSteels()
                    } else {
                        wrapEagleWithBricks()
                    }
                }
            }
        }
        if (botsFrozenTimer.isActive) {
            botsFrozenTimer.tick(tick)
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
        if (duration >= 0 && duration > remainingFortificationTimer.remainingTime) {
            remainingFortificationTimer.resetAndActivate(duration)
            wrapEagleWithSteels()
        } else {
            wrapEagleWithBricks()
        }
    }

    fun freezeBots() {
        botsFrozenTimer.resetAndActivate()
    }

    fun destroyEagle() {
        eagle = eagle.copy(dead = true)
        gameState.setGameResult(BattleResult.Lost)
    }

    fun deductRemainingBot() {
        remainingBot = max(remainingBot - 1, 0)
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