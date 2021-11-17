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
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.ui.components.mpx2dp
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

@Composable
fun rememberBulletState(
    mapState: MapState,
): BulletState {
    return remember { BulletState(mapState) }
}

class BulletState(
    private val mapState: MapState,
) : TickListener {
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
            }

        }
        bullets = newBullets

        handleCollisionWithBorder()
        handleCollisionBetweenBullets(tick)
        handleCollisionWithBricks()
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

    private fun handleCollisionBetweenBullets(tick: Tick) {
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
                } else {
                    // when the fps is low or bullets too fast, they may miss the rect collision test.
                    // this branch is to check possible bullet collision between ticks
                    val b1Flashback = b1.getFlashbackBullet(tick.delta)
                    val b2Flashback = b2.getFlashbackBullet(tick.delta)

                    val b1Trajectory = b1Flashback.let {
                        val left = min(it.x, b1.x)
                        val right = max(it.x, b1.x)
                        val top = min(it.y, b1.y)
                        val bottom = max(it.y, b1.y)
                        Rect(
                            topLeft = Offset(left, top),
                            bottomRight = Offset(right, bottom) + Offset(BULLET_COLLISION_SIZE, BULLET_COLLISION_SIZE)
                        )
                    }

                    val b2Trajectory = b2Flashback.let {
                        val left = min(it.x, b2.x)
                        val right = max(it.x, b2.x)
                        val top = min(it.y, b2.y)
                        val bottom = max(it.y, b2.y)
                        Rect(
                            topLeft = Offset(left, top),
                            bottomRight = Offset(right, bottom) + Offset(BULLET_COLLISION_SIZE, BULLET_COLLISION_SIZE)
                        )
                    }

                    if (b1Trajectory.overlaps(b2Trajectory)) {
                        // if the two bullets crossed, check if they have hit each other in between ticks
                        val collisionArea = b1Trajectory.intersect(b2Trajectory)
                        val b1tt = b1Flashback.travelTimeInArea(collisionArea)
                        val b2tt = b2Flashback.travelTimeInArea(collisionArea)
                        val touched = (b1tt intersect b2tt).isNotEmpty()
                        if (touched) {
                            collisions = collisions.toMutableMap().apply {
                                put(b1.id, true)
                                put(b2.id, true)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleCollisionWithBricks() {
        bullets.values.forEach { bullet ->
            val bricksHit = BrickElement.getIndicesInRect(bullet.collisionBox, moveDirection = bullet.direction)
            if (bricksHit.isNotEmpty()) {
                val destroyed = mapState.destroyBricksIndex(bricksHit.toSet())
                if (destroyed) {
                    removeBullet(bullet.id)
                }
            }
        }
    }

    private fun Bullet.travelTimeInArea(rect: Rect): LongRange {
        val flyInDistance = when (direction) {
            Direction.Up -> top - rect.bottom
            Direction.Down -> rect.top - bottom
            Direction.Left -> left - rect.right
            Direction.Right -> rect.left - right
        }.coerceAtLeast(0f)
        // when the bullet is already in the area, the distance will be negative, so set it 0.

        val flyOutDistance = when (direction) {
            Direction.Up, Direction.Down -> flyInDistance + rect.height
            Direction.Left, Direction.Right -> flyInDistance + rect.width
        }
        return (flyInDistance / speed).roundToLong()..(flyOutDistance / speed).roundToLong()
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

    val left: MapPixel = x
    val top: MapPixel = y
    val right: MapPixel = x + BULLET_COLLISION_SIZE
    val bottom: MapPixel = y + BULLET_COLLISION_SIZE

    fun getFlashbackBullet(millisAgo: Long): Bullet {
        val delta = millisAgo * speed
        val currPos = Offset(x, y)
        val oldPos = when (direction) {
            Direction.Up -> currPos + Offset(0f, delta)
            Direction.Down -> currPos - Offset(0f, delta)
            Direction.Left -> currPos + Offset(delta, 0f)
            Direction.Right -> currPos - Offset(delta, 0f)
        }
        return copy(x = oldPos.x, y = oldPos.y)
    }
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