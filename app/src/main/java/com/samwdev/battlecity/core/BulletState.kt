package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.anyRealElements
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

@Composable
fun rememberBulletState(
    mapState: MapState,
    tankState: TankState,
    explosionState: ExplosionState,
    soundState: SoundState,
): BulletState {
    return remember { BulletState(mapState, tankState, explosionState, soundState) }
}

class BulletState(
    private val mapState: MapState,
    private val tankState: TankState,
    private val explosionState: ExplosionState,
    private val soundState: SoundState,
) : TickListener {
    private val nextId: AtomicInteger = AtomicInteger(0)
    var friendlyFire = false

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

        checkCollisionWithBorder()
        checkCollisionWithBullets(tick)
        checkCollisionWithBricks(tick)
        checkCollisionWithSteels(tick)
        checkCollisionWithTanks(tick)
        checkCollisionWithEagle(tick)

        handleTrajectoryCollisionInfo()
    }

    fun fire(tank: Tank) {
        bullets = bullets.toMutableMap().apply {
            val bulletOrigin = tank.bulletStartPosition
            put(nextId.incrementAndGet(), Bullet(
                id = nextId.get(),
                direction = tank.direction,
                speed = tank.bulletSpeed,
                x = bulletOrigin.x,
                y = bulletOrigin.y,
                power = tank.bulletPower,
                ownerTankId = tank.id,
                side = tank.side,
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

            val firstCollision = when (bullet.direction) {
                Direction.Up -> bottomMostCollision
                Direction.Down -> topMostCollision
                Direction.Left -> rightMostCollision
                Direction.Right -> leftMostCollision
            }!!
            val impactArea = bullet.getImpactAreaIfHitAt(firstCollision.hitPoint)

            // bricks and steels can be destroyed by bullet explosion, so check them regardless of being directly hit
            val brickIndices = BrickElement.getIndicesOverlappingRect(impactArea, bullet.direction)
            val hitAnyBricks = brickIndices.anyRealElements(mapState.bricks)
            if (hitAnyBricks) {
                soundState.playSound(SoundEffect.BulletHitBrick)
                mapState.destroyBricksIndex(brickIndices.toSet())
            }
            val steelIndices = SteelElement.getIndicesOverlappingRect(impactArea, bullet.direction)
            val hitAnySteels = steelIndices.anyRealElements(mapState.steels)
            if (hitAnySteels) {
                soundState.playSound(SoundEffect.BulletHitSteel)
                if (bullet.power >= SteelElement.strength) {
                    mapState.destroySteels(steelIndices.toSet())
                }
            }
            when (firstCollision) {
                is HitBorder -> {
                    soundState.playSound(SoundEffect.BulletHitSteel)
                }
                is HitTank -> {
                    soundState.playSound(SoundEffect.BulletHitSteel)
                    tankState.hit(bullet, firstCollision.tank)
                }
                is HitBullet -> {

                }
            }
            explosionState.spawnExplosion(firstCollision.hitPoint, ExplosionAnimationSmall)
        }
        removeCollidedBullets(trajectoryCollisionInfo.keys)
        trajectoryCollisionInfo = emptyMap()
    }


    private fun checkCollisionWithBorder() {
        bullets.values.forEach { bullet ->
            if (bullet.x <= 0 || bullet.x >= MAP_BLOCK_COUNT.grid2mpx || bullet.y <= 0 ||
                bullet.y >= MAP_BLOCK_COUNT.grid2mpx) {
                addBulletTrajectoryCollision(HitBorder(bullet))
            }
        }
    }

    private fun checkCollisionWithBullets(tick: Tick) {
        val all = bullets.values.toList()
        for (i in 0 until all.size - 1) {
            val b1 = all[i]
            for (j in i + 1 until all.size) {
                val b2 = all[j]
                if (b1.collisionBox.overlaps(b2.collisionBox)) {
                    val hitRect = b1.collisionBox.intersect(b2.collisionBox)
                    addBulletTrajectoryCollision(HitBullet(bullet = b1, otherBullet = b2, hitPoint = hitRect.center))
                    addBulletTrajectoryCollision(HitBullet(bullet = b2, otherBullet = b1, hitPoint = hitRect.center))
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
                            addBulletTrajectoryCollision(HitBullet(bullet = b2, otherBullet = b1, hitPoint = collisionArea.center))
                        }
                    }
                }
            }
        }
    }

    private fun checkCollisionWithBricks(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            BrickElement.getHitPoint(mapState.bricks, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitBrick(bullet = bullet, hitPoint = it))
            }
        }
    }

    private fun checkCollisionWithSteels(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            SteelElement.getHitPoint(mapState.steels, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitSteel(bullet = bullet, hitPoint = it))
            }
        }
    }

    private fun checkCollisionWithTanks(tick: Tick) {
        bullets.values.forEach { bullet ->
            tankState.tanks.filter { (it.value.id != bullet.ownerTankId) && (friendlyFire || bullet.side != it.value.side) }
                .forEach { (_, tank) ->
                    val trajectory = bullet.getTrajectory(tick.delta)
                    if (trajectory.overlaps(tank.collisionBox)) {
                        val intersect = trajectory.intersect(tank.collisionBox)
                        val impactPoint = when (bullet.direction) {
                            Direction.Up -> Offset(bullet.center.x, intersect.bottom)
                            Direction.Down -> Offset(bullet.center.x, intersect.top)
                            Direction.Left -> Offset(intersect.right, bullet.center.y)
                            Direction.Right -> Offset(intersect.left, bullet.center.y)
                        }
                        addBulletTrajectoryCollision(HitTank(bullet = bullet, hitPoint = impactPoint,tank = tank))
                    }
                }
        }
    }

    private fun checkCollisionWithEagle(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            EagleElement.getHitPoint(mapState.eagle, trajectory, bullet.direction)?.let {
                mapState.destroyEagle()
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
    val side: TankSide,
) {
    val collisionBox: Rect = Rect(offset = Offset(x, y), size = Size(BULLET_COLLISION_SIZE, BULLET_COLLISION_SIZE))
    val center: Offset = collisionBox.center
    var explosionRadius: MapPixel = BULLET_COLLISION_SIZE * power

    val left: MapPixel = x
    val top: MapPixel = y
    val right: MapPixel = x + BULLET_COLLISION_SIZE
    val bottom: MapPixel = y + BULLET_COLLISION_SIZE

    fun getImpactAreaIfHitAt(hitPoint: Offset): Rect {
        // todo rework
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

//        val impactRadiusOffset = (explosionRadius - BULLET_COLLISION_SIZE) / 2
//        if (direction.isVertical()) {
//            left -= impactRadiusOffset
//            right += impactRadiusOffset
//        } else {
//            top -= impactRadiusOffset
//            bottom += impactRadiusOffset
//        }

        return Rect(
            topLeft = Offset(left, top),
            bottomRight = Offset(right, bottom)
        )
    }
}

private val Bullet.hitPointIfHitBorder: Offset get() =
    when (direction) {
        Direction.Up -> Offset(center.x, 0f)
        Direction.Down -> Offset(center.x, MAP_BLOCK_COUNT.grid2mpx)
        Direction.Left -> Offset(0f, center.y)
        Direction.Right -> Offset(MAP_BLOCK_COUNT.grid2mpx, center.y)
    }

private sealed class TrajectoryCollisionInfo(open val bullet: Bullet, open val hitPoint: Offset)

private data class HitBorder(override val bullet: Bullet) :
    TrajectoryCollisionInfo(bullet, bullet.hitPointIfHitBorder)

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