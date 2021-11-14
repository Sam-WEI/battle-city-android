package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.ui.components.LocalMapUnitDp
import com.samwdev.battlecity.ui.components.mu
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun rememberBulletState(): BulletState {
    return remember { BulletState() }
}

class BulletState : TickListener {
    private val nextId: AtomicInteger = AtomicInteger(0)

    var bullets by mutableStateOf<Map<BulletId, Bullet>>(mapOf())
        private set

    override fun onTick(tick: Tick) {
        val newBullets = bullets.keys.associateWith { id ->
            val bullet = bullets[id]!!
            val delta = tick.delta * bullet.speed
            when (bullet.direction) {
                Direction.Up -> bullet.copy(y = bullet.y - delta)
                Direction.Down -> bullet.copy(y = bullet.y + delta)
                Direction.Left ->  bullet.copy(x = bullet.x - delta)
                Direction.Right ->  bullet.copy(x = bullet.x + delta)
                Direction.Unspecified -> throw IllegalStateException("A bullet must have a direction.")
            }

        }
        bullets = newBullets
    }

    fun addBullet(tank: Tank) {
        bullets = bullets.toMutableMap().apply {
            val bulletOrigin = tank.getBulletStartPosition()
            put(nextId.incrementAndGet(), Bullet(
                id = nextId.get(),
                direction = tank.direction,
                speed = 0.02f,
                x = bulletOrigin.x.value,
                y = bulletOrigin.y.value,
                power = 1,
                ownerTankId = tank.id,
            ))
        }
    }
}

typealias BulletId = Int

data class Bullet(
    val id: BulletId,
    val direction: Direction,
    val speed: Float,
    val x: Float,
    val y: Float,
    val power: Int = 1,
    val ownerTankId: TankId,
)

val defaultBullet = Bullet(
    1, Direction.Down, 1f, 0.5f, 0.5f, 2, 1
)

@Composable
fun Bullet(bullet: Bullet) {
    val mu = LocalMapUnitDp.current.value
    Canvas(modifier = Modifier
        .size(0.2f.mu, 0.2f.mu)
        .offset(bullet.x.mu, bullet.y.mu)
    ) {
        drawRect(Color.Red, topLeft = Offset.Zero, size = size)
    }
}