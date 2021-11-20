package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.MapElements

@Composable
fun rememberMapState(mapElements: MapElements): MapState {
    return remember(mapElements) { MapState(mapElements) }
}

class MapState(
    mapElements: MapElements,
) : TickListener {
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

    override fun onTick(tick: Tick) {

    }

    fun destroyBricks(indices: Set<BrickElement>) {
        bricks = bricks.filter { it !in indices }
    }

    fun destroyBricksIndex(indices: Set<Int>): Boolean {
        val oldCount = bricks.count()
        bricks = bricks.filter { it.index !in indices }
        val newCount = bricks.count()
        return newCount != oldCount
    }

    fun destroySteels(indices: Set<Int>): Boolean {
        val oldCount = steels.count()
        steels = steels.filter { it.index !in indices }
        val newCount = steels.count()
        return newCount != oldCount
    }

    fun destroyEagle() {
        eagle = eagle.copy(dead = true)
    }
}