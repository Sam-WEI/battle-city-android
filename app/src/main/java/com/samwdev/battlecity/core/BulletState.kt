package com.samwdev.battlecity.core

import androidx.compose.runtime.*

@Composable
fun rememberBulletState(): BulletState {
    return remember { BulletState() }
}

class BulletState {
    var bullets by mutableStateOf<Map<Long, Bullet>>(mapOf(), policy = referentialEqualityPolicy())
        private set

    fun onTick(tick: Tick) {

    }

    fun addBullet(tank: Tank) {

    }
}

data class Bullet(
    val id: Int,
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