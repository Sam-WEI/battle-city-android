package com.samwdev.battlecity.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
                speed = 0.05f,
                x = bulletOrigin.x,
                y = bulletOrigin.y,
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
    val speed: MapPixel,
    val x: MapPixel,
    val y: MapPixel,
    val power: Int = 1,
    val ownerTankId: TankId,
)

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