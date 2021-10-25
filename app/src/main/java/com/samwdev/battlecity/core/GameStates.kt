package com.samwdev.battlecity.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberGameState(
    tank: TanksViewModel = remember { TanksViewModel() },
) = remember(tank) { GameState(tank.tanks.value) }

data class GameState(
    val tanks: Map<String, TankState>,
)

data class TankState(
    val x: Int,
    val y: Int,
    val direction: Direction = Direction.Up,
)

enum class Direction {
    Up, Down, Left, Right, Unspecified
}