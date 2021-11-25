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
    }
    // todo move to a proper place
    private var remainingFortificationTime: Int = 0

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

    val iceIndexSet: Set<Int> = ices.map { it.index }.toSet()

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
        bricks = bricks.filter { it.index !in indices }
        val newCount = bricks.count()
        return newCount != oldCount
    }

    fun destroySteelsIndex(indices: Set<Int>): Boolean {
        val oldCount = steels.count()
        steels = steels.filter { it.index !in indices }
        val newCount = steels.count()
        return newCount != oldCount
    }

    fun fortifyBase() {
        remainingFortificationTime = FortificationDuration
        wrapEagleWithSteels()
    }

    private fun wrapEagleWithSteels() {
        destroyBricksIndex(brickIndicesAroundEagle)
        steels = steels.toMutableList()
            .apply { addAll(steelIndicesAroundEagle.map { SteelElement(it) }) }
    }

    private fun wrapEagleWithBricks() {
        destroySteelsIndex(steelIndicesAroundEagle)
        bricks = bricks.toMutableList()
            .apply { addAll(brickIndicesAroundEagle.map { BrickElement(it) }) }
    }

    fun destroyEagle() {
        eagle = eagle.copy(dead = true)
    }
}