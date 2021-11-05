package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PlayerState(
    val name: String = "player_1",
    lives: Int = 3,
    score: Int = 0,
    tankId: Int,
) {
    var lives by mutableStateOf(lives)
        private set
    var score by mutableStateOf(score)
        private set
    var tankId by mutableStateOf(tankId)
        private set
}