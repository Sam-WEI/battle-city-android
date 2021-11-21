package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.anyRealElements
import java.util.concurrent.atomic.AtomicInteger
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
        if (tank.side == TankSide.Player) {
            soundState.playSound(SoundEffect.BulletShot)
        }
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
                if (bullet.side == TankSide.Player) {
                    soundState.playSound(SoundEffect.BulletHitBrick)
                }
                mapState.destroyBricksIndex(brickIndices.toSet())
            }
            val steelIndices = SteelElement.getIndicesOverlappingRect(impactArea, bullet.direction)
            val hitAnySteels = steelIndices.anyRealElements(mapState.steels)
            if (hitAnySteels) {
                if (bullet.side == TankSide.Player) {
                    soundState.playSound(SoundEffect.BulletHitSteel)
                }
                if (bullet.power >= SteelElement.strength) {
                    mapState.destroySteels(steelIndices.toSet())
                }
            }

            if (impactArea.overlaps(mapState.eagle.rect)) {
                // can be either a direct hit or an AOE impact.
                mapState.destroyEagle()
                soundState.playSound(SoundEffect.Explosion2)
            }

            when (firstCollision) {
                is HitBorder -> {
                    if (bullet.side == TankSide.Player) {
                        soundState.playSound(SoundEffect.BulletHitSteel)
                    }
                }
                is HitTank -> {
                    val afterHit = firstCollision.tank.hitBy(bullet)
                    if (bullet.side == TankSide.Player
                        && firstCollision.tank.side == TankSide.Bot
                        && afterHit.isAlive) {
                        soundState.playSound(SoundEffect.BulletHitSteel) // todo should be a special dink sound
                    }
                    tankState.hit(bullet, firstCollision.tank)

                    if (afterHit.isDead) {
                        explosionState.spawnExplosion(firstCollision.tank.collisionBox.center, ExplosionAnimationBig)
                        if (firstCollision.tank.side == TankSide.Bot) {
                            soundState.playSound(SoundEffect.Explosion1)
                        } else {
                            soundState.playSound(SoundEffect.Explosion2)
                        }
                    }
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
            tankState.tanks.asSequence()
                .filter { it.value.id != bullet.ownerTankId }
                .filterNot { !friendlyFire && bullet.side == it.value.side }
                .filter { !it.value.isSpawning }
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
                addBulletTrajectoryCollision(HitEagle(bullet = bullet, hitPoint = it))
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

private val Bullet.hitPointIfHitBorder: Offset
    get() =
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

private data class HitEagle(override val bullet: Bullet, override val hitPoint: Offset) :
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