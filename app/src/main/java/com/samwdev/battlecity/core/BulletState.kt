package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue

class BulletState {
    var bullets by mutableStateOf<Map<Long, Bullet>>(mapOf(), policy = referentialEqualityPolicy())
        private set

    fun addBullet(tankId: Long) {

    }
}

data class Bullet(
    val id: Long,
    val direction: Direction,
    val speed: Float,
    val x: Float,
    val y: Float,
    val power: Int,
    val owner: Long,
)

val defaultBullet = Bullet(
    1, Direction.Down, 1f, 0.5f, 0.5f, 2, 1
)