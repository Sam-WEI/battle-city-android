package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.anyRealElements
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

@Composable
fun rememberBulletState(
    mapState: MapState,
    explosionState: ExplosionState,
    soundState: SoundState,
): BulletState {
    return remember { BulletState(mapState, explosionState, soundState) }
}

class BulletState(
    private val mapState: MapState,
    private val explosionState: ExplosionState,
    private val soundState: SoundState,
) : TickListener {
    private val nextId: AtomicInteger = AtomicInteger(0)

    var bullets by mutableStateOf<Map<BulletId, Bullet>>(mapOf())
        private set

    private var trajectoryCollisionInfo by mutableStateOf<Map<Bullet, List<TrajectoryCollisionInfo>>>(mapOf())

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
        handleCollisionWithBricks(tick)
        handleCollisionWithSteels(tick)
        handleTrajectoryCollisionInfo()
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
                power = tank.bulletPower,
                ownerTankId = tank.id,
            ))
        }
        soundState.playSound(SoundEffect.BulletShot)
    }

    fun countBulletForTank(tankId: TankId) = bullets.count { it.value.ownerTankId == tankId }

    private fun removeCollidedBullets(set: Set<Bullet>) {
        bullets = bullets.filter { it.value !in set }
    }

    private fun addBulletTrajectoryCollision(trajectoryCollision: TrajectoryCollisionInfo) {
        val oldList = trajectoryCollisionInfo.getOrElse(trajectoryCollision.bullet) { emptyList() }
        trajectoryCollisionInfo = trajectoryCollisionInfo.toMutableMap().apply {
            put(trajectoryCollision.bullet, oldList + trajectoryCollision)
        }
    }

    private fun handleTrajectoryCollisionInfo() {
        trajectoryCollisionInfo.entries.forEach { (bullet, info) ->
            var leftMostCollision: TrajectoryCollisionInfo? = null
            var rightMostCollision: TrajectoryCollisionInfo? = null
            var topMostCollision: TrajectoryCollisionInfo? = null
            var bottomMostCollision: TrajectoryCollisionInfo? = null
            info.forEach {
                if (leftMostCollision == null || it.hitPoint.x < leftMostCollision!!.hitPoint.x) {
                    leftMostCollision = it
                }
                if (rightMostCollision == null || it.hitPoint.x > rightMostCollision!!.hitPoint.x) {
                    rightMostCollision = it
                }
                if (topMostCollision == null || it.hitPoint.y < topMostCollision!!.hitPoint.y) {
                    topMostCollision = it
                }
                if (bottomMostCollision == null || it.hitPoint.y > bottomMostCollision!!.hitPoint.y) {
                    bottomMostCollision = it
                }
            }

            val firstHitPoint = when (bullet.direction) {
                Direction.Up -> bottomMostCollision
                Direction.Down -> topMostCollision
                Direction.Left -> rightMostCollision
                Direction.Right -> leftMostCollision
            }!!
            val impactedArea = bullet.getImpactedAreaIfExplodeAt(firstHitPoint.hitPoint)
            val brickIndices = BrickElement.getIndicesOverlappingRect(impactedArea, bullet.direction)
            val hitAnyBricks = brickIndices.anyRealElements(mapState.bricks)
            if (hitAnyBricks) {
                mapState.destroyBricksIndex(brickIndices.toSet())
                soundState.playSound(SoundEffect.BulletHitBrick)
            }
            val steelIndices = SteelElement.getIndicesOverlappingRect(impactedArea, bullet.direction)
            val hitAnySteels = steelIndices.anyRealElements(mapState.steels)
            if (hitAnySteels) {
                soundState.playSound(SoundEffect.BulletHitSteel)
                if (bullet.power >= SteelElement.strength) {
                    mapState.destroySteels(steelIndices.toSet())
                }
            }
            if (firstHitPoint is HitBorder) {
                soundState.playSound(SoundEffect.BulletHitSteel)
            }
            explosionState.spawnExplosion(firstHitPoint.hitPoint, ExplosionAnimationSmall)
        }
        removeCollidedBullets(trajectoryCollisionInfo.keys)
        trajectoryCollisionInfo = emptyMap()
    }


    private fun handleCollisionWithBorder() {
        bullets.values.forEach { bullet ->
            if (bullet.x <= 0 || bullet.x >= MAP_BLOCK_COUNT.grid2mpx || bullet.y <= 0 ||
                bullet.y >= MAP_BLOCK_COUNT.grid2mpx) {
                addBulletTrajectoryCollision(HitBorder(bullet))
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
                    val hitRect = b1.collisionBox.intersect(b2.collisionBox)
                    addBulletTrajectoryCollision(HitBullet(bullet = b1, otherBullet = b2, hitPoint = hitRect.center))
                } else {
                    // when the fps is low or bullets too fast, they may miss the rect collision test between ticks.
                    // this branch is to check possible bullet collision between ticks
                    val b1Flashback = b1.getFlashbackBullet(tick.delta)
                    val b2Flashback = b2.getFlashbackBullet(tick.delta)

                    val b1Trajectory = b1Flashback.getTrajectory(b1)
                    val b2Trajectory = b2Flashback.getTrajectory(b2)

                    if (b1Trajectory.overlaps(b2Trajectory)) {
                        // if the two bullets crossed, check if they have hit each other in between ticks
                        val collisionArea = b1Trajectory.intersect(b2Trajectory)
                        val b1tt = b1Flashback.travelTimeInArea(collisionArea)
                        val b2tt = b2Flashback.travelTimeInArea(collisionArea)
                        val touched = (b1tt intersect b2tt).isNotEmpty()
                        if (touched) {
                            addBulletTrajectoryCollision(HitBullet(bullet = b1, otherBullet = b2, hitPoint = collisionArea.center))
                        }
                    }
                }
            }
        }
    }

    private fun handleCollisionWithBricks(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            BrickElement.getHitPoint(mapState.bricks, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitBrick(bullet = bullet, hitPoint = it))
            }
        }
    }

    private fun handleCollisionWithSteels(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            SteelElement.getHitPoint(mapState.steels, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitSteel(bullet = bullet, hitPoint = it))
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
    val center: Offset = collisionBox.center
    var explosionRadius: MapPixel = BULLET_COLLISION_SIZE * power

    val left: MapPixel = x
    val top: MapPixel = y
    val right: MapPixel = x + BULLET_COLLISION_SIZE
    val bottom: MapPixel = y + BULLET_COLLISION_SIZE

    fun getImpactedAreaIfExplodeAt(hitPoint: Offset): Rect {
        val impactedAreaCenter = when (direction) {
            Direction.Up -> hitPoint - Offset(0f, explosionRadius)
            Direction.Down -> hitPoint + Offset(0f, explosionRadius)
            Direction.Left -> hitPoint - Offset(explosionRadius, 0f)
            Direction.Right -> hitPoint + Offset(explosionRadius, 0f)
        }
        return Rect(center = hitPoint, radius = explosionRadius)
    }

    fun getFlashbackBullet(millisAgo: Long): Bullet {
        val delta = millisAgo * speed
        val currPos = Offset(x, y)
        val oldPos = when (direction) {
            Direction.Up -> currPos + Offset(0f, delta)
            Direction.Down -> currPos - Offset(0f, delta)
            Direction.Left -> currPos + Offset(delta, 0f)
            Direction.Right -> currPos - Offset(delta, 0f)
        }
        return copy(x = oldPos.x.coerceAtLeast(0f), y = oldPos.y.coerceAtLeast(0f))
    }

    fun getTrajectory(vararg flashbackDeltas: Long): Rect {
        val bullets = flashbackDeltas.map { getFlashbackBullet(it) }.toTypedArray()
        return getTrajectory(*bullets)
    }

    /** Trajectory is the travel path calculated from the bullet's current and past positions */
    fun getTrajectory(vararg flashbacks: Bullet): Rect {
        val all = flashbacks.toMutableList().also { it.add(this) }
        var left = Float.MAX_VALUE
        var right = Float.MIN_VALUE
        var top = Float.MAX_VALUE
        var bottom = Float.MIN_VALUE
        all.forEach {
            left = min(left, it.x)
            right = max(right, it.x)
            top = min(top, it.y)
            bottom = max(bottom, it.y)
        }
        right += BULLET_COLLISION_SIZE
        bottom += BULLET_COLLISION_SIZE

        val impactRadiusOffset = (explosionRadius - BULLET_COLLISION_SIZE) / 2
        if (direction.isVertical()) {
            left -= impactRadiusOffset
            right += impactRadiusOffset
        } else {
            top -= impactRadiusOffset
            bottom += impactRadiusOffset
        }

        return Rect(
            topLeft = Offset(left, top),
            bottomRight = Offset(right, bottom)
        )
    }
}

private val Bullet.borderHitPoint: Offset get() =
    when (direction) {
        Direction.Up -> Offset(center.x, 0f)
        Direction.Down -> Offset(center.x, MAP_BLOCK_COUNT.grid2mpx)
        Direction.Left -> Offset(0f, center.y)
        Direction.Right -> Offset(MAP_BLOCK_COUNT.grid2mpx, center.y)
    }

private sealed class TrajectoryCollisionInfo(open val bullet: Bullet, open val hitPoint: Offset)

private data class HitBorder(override val bullet: Bullet) :
    TrajectoryCollisionInfo(bullet, bullet.borderHitPoint)

private data class HitBrick(override val bullet: Bullet, override val hitPoint: Offset) :
    TrajectoryCollisionInfo(bullet, hitPoint)

private data class HitSteel(override val bullet: Bullet, override val hitPoint: Offset) :
    TrajectoryCollisionInfo(bullet, hitPoint)

private data class HitTank(
    override val bullet: Bullet,
    override val hitPoint: Offset,
    val tank: Tank,
) : TrajectoryCollisionInfo(bullet, hitPoint)

private data class HitBullet(
    override val bullet: Bullet,
    val otherBullet: Bullet,
    override val hitPoint: Offset,
) : TrajectoryCollisionInfo(bullet, hitPoint)