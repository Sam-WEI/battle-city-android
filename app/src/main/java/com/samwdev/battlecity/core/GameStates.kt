package com.samwdev.battlecity.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberGameState(
    tank: TankModel,
) = remember(tank) { GameState(tank) }

data class GameState(
    val tank: TankModel,
)

data class TankModel(
    val x: Int,
    val y: Int,
    val direction: Direction,
)

enum class Direction {
    Up, Down, Left, Right
}