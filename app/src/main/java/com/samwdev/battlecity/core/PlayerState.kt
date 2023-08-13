package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PlayerState(
    val name: String = "player_1",
    lives: Int = 3,
    score: Int = 0,
    tankId: Int,
) {
    var lives by mutableIntStateOf(lives)
        private set
    var score by mutableIntStateOf(score)
        private set
    var tankId by mutableIntStateOf(tankId)
        private set
}