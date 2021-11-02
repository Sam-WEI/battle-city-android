package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import com.samwdev.battlecity.entity.MapElements

@Composable
fun rememberMapState(mapElements: MapElements): MapState {
    return remember(mapElements) { MapState(mapElements) }
}

class MapState(
    mapElements: MapElements,
) {
    var bricks by mutableStateOf(mapElements.bricks)
        private set

    var steels by mutableStateOf(mapElements.steels)
        private set

    var trees by mutableStateOf(mapElements.trees)
        private set

    var waters by mutableStateOf(mapElements.waters)
        private set

    var ices by mutableStateOf(mapElements.ices)
        private set

    fun destroyBricks(indices: Set<Int>) {
        bricks = bricks.filter { it !in indices }
    }

    fun destroySteels(indices: Set<Int>) {
        steels = steels.filter { it !in indices }
    }
}