package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import com.samwdev.battlecity.ui.components.LocalMapPixelDp
import com.samwdev.battlecity.ui.components.mpx2dp
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun rememberBulletState(): BulletState {
    return remember { BulletState() }
}

class BulletState : TickListener {
    private val nextId: AtomicInteger = AtomicInteger(0)

    var bullets by mutableStateOf<Map<BulletId, Bullet>>(mapOf())
        private set

    var collisions by mutableStateOf<Map<BulletId, Boolean>>(mapOf())

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

        handleCollisionWithBorder()
        handleCollisionBetweenBullets()
        removeCollidedBullets()
    }

    fun fire(tank: Tank) {
        bullets = bullets.toMutableMap().apply {
            val bulletOrigin = tank.getBulletStartPosition()
            put(nextId.incrementAndGet(), Bullet(
                id = nextId.get(),
                direction = tank.direction,
                speed = 0.3f,
                x = bulletOrigin.x,
                y = bulletOrigin.y,
                power = 1,
                ownerTankId = tank.id,
            ))
        }
    }

    fun removeBullet(bulletId: BulletId) {
        bullets = bullets.filter { it.key != bulletId }
    }

    fun getBulletCountForTank(tankId: TankId) = bullets.count { it.value.ownerTankId == tankId }

    private fun handleCollisionWithBorder() {
        bullets.values.forEach { bullet ->
            if (bullet.x <= 0) {
                collisions = collisions.toMutableMap().apply {
                    put(bullet.id, true)
                }
            }
            if (bullet.x >= MAP_BLOCK_COUNT.grid2mpx) {
                collisions = collisions.toMutableMap().apply {
                    put(bullet.id, true)
                }
            }
            if (bullet.y <= 0) {
                collisions = collisions.toMutableMap().apply {
                    put(bullet.id, true)
                }
            }
            if (bullet.y >= MAP_BLOCK_COUNT.grid2mpx) {
                collisions = collisions.toMutableMap().apply {
                    put(bullet.id, true)
                }
            }
        }
    }

    private fun handleCollisionBetweenBullets() {
        val all = bullets.values.toList()
        for (i in 0 until all.size - 1) {
            val b1 = all[i]
            for (j in i + 1 until all.size) {
                val b2 = all[j]
                if (b1.collisionBox.overlaps(b2.collisionBox)) {
                    collisions = collisions.toMutableMap().apply {
                        put(b1.id, true)
                        put(b2.id, true)
                    }
                }
            }
        }
    }

    private fun removeCollidedBullets() {
        collisions.keys.forEach { id ->
            removeBullet(id)
        }
    }
}

typealias BulletId = Int

data class Bullet(
    val id: BulletId,
    val direction: Direction,
    val speed: MapPixel,
    val x: MapPixel,
    val y: MapPixel,
    val power: Int = 1,
    val ownerTankId: TankId,
) {
    val collisionBox: Rect = Rect(offset = Offset(x, y), size = Size(BULLET_COLLISION_SIZE, BULLET_COLLISION_SIZE))
}

private val BulletColor = Color(0xFFADADAD)

@Composable
fun Bullet(bullet: Bullet) {
    val oneMapPixel = 1f.mpx2dp
    Canvas(modifier = Modifier
        .size(BULLET_COLLISION_SIZE.mpx2dp, BULLET_COLLISION_SIZE.mpx2dp)
        .offset(bullet.x.mpx2dp, bullet.y.mpx2dp)
        .rotate(bullet.direction.degree)
    ) {
        // bullet body
        drawRect(
            color = BulletColor,
            topLeft = Offset.Zero,
            size = size,
        )
        // bullet tip
        drawRect(
            color = BulletColor,
            topLeft = Offset(1f * oneMapPixel.toPx(), -1f * oneMapPixel.toPx()),
            size = Size(oneMapPixel.toPx(), oneMapPixel.toPx())
        )
    }
}