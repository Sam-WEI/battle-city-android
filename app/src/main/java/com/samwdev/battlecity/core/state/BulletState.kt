package com.samwdev.battlecity.core.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.samwdev.battlecity.core.Bullet
import com.samwdev.battlecity.core.BulletId
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.GridSizeAware
import com.samwdev.battlecity.core.SoundEffect
import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.core.TankId
import com.samwdev.battlecity.core.TankSide
import com.samwdev.battlecity.core.TickListener
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.core.hitBy
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.anyRealElements
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToLong

class BulletState(
    private val mapState: MapState,
    private val tankState: TankState,
    private val explosionState: ExplosionState,
    private val soundState: SoundState,
) : TickListener(), GridSizeAware by mapState {
    private val idGen: AtomicInteger = AtomicInteger(0)
    var friendlyFire = false

    var bullets by mutableStateOf<Map<BulletId, Bullet>>(mapOf())
        private set

    private var trajectoryCollisionInfo by mutableStateOf<Map<Bullet, List<TrajectoryCollisionInfo>>>(mapOf())

    override fun onTick(tick: Tick) {
        bullets = bullets.mapValues { (_, bullet) ->
            val delta = tick.delta * bullet.speed
            when (bullet.direction) {
                Direction.Up -> bullet.copy(y = bullet.y - delta)
                Direction.Down -> bullet.copy(y = bullet.y + delta)
                Direction.Left ->  bullet.copy(x = bullet.x - delta)
                Direction.Right ->  bullet.copy(x = bullet.x + delta)
            }
        }

        checkCollisionWithBorder()
        checkCollisionWithBullets(tick)
        checkCollisionWithBricks(tick)
        checkCollisionWithSteels(tick)
        checkCollisionWithTanks(tick)

        if (!mapState.eagle.dead) {
            checkCollisionWithEagle(tick)
        }

        handleTrajectoryCollisionInfo()
    }

    fun fire(tank: Tank) {
        if (tank.remainingCooldown > 0 || countBulletForTank(tank.id) >= tank.maxBulletCount) {
            return
        }
        bullets = bullets.toMutableMap().apply {
            val bulletOrigin = tank.bulletStartPosition
            put(idGen.incrementAndGet(), Bullet(
                id = idGen.get(),
                direction = tank.facingDirection,
                speed = tank.bulletSpeed,
                x = bulletOrigin.x,
                y = bulletOrigin.y,
                power = tank.bulletPower,
                ownerTankId = tank.id,
                side = tank.side,
            ))
        }
        tankState.startFireCooldown(tank.id)
        if (tank.side == TankSide.Player) {
            soundState.playSound(SoundEffect.Shoot)
        } else {
//            soundState.playSound(SoundEffect.Shoot)
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
                if (leftMostCollision == null || it.impactPoint.x < leftMostCollision!!.impactPoint.x) {
                    leftMostCollision = it
                }
                if (rightMostCollision == null || it.impactPoint.x > rightMostCollision!!.impactPoint.x) {
                    rightMostCollision = it
                }
                if (topMostCollision == null || it.impactPoint.y < topMostCollision!!.impactPoint.y) {
                    topMostCollision = it
                }
                if (bottomMostCollision == null || it.impactPoint.y > bottomMostCollision!!.impactPoint.y) {
                    bottomMostCollision = it
                }
            }

            val firstCollision = when (bullet.direction) {
                Direction.Up -> bottomMostCollision
                Direction.Down -> topMostCollision
                Direction.Left -> rightMostCollision
                Direction.Right -> leftMostCollision
            }!!
            val impactArea = bullet.getImpactAreaIfHitAt(firstCollision.impactPoint)

            // bricks and steels can be destroyed by bullet explosion, so check them regardless of being directly hit
            val brickIndices = BrickElement.getOverlapIndicesInRect(impactArea, hGridSize, bullet.direction)
            val hitAnyBricks = brickIndices.anyRealElements(mapState.bricks)
            if (hitAnyBricks) {
                if (bullet.side == TankSide.Player) {
                    soundState.playSound(SoundEffect.HitBrick)
                }
                mapState.destroyBricksIndex(brickIndices.toSet())
            }
            val steelIndices = SteelElement.getOverlapIndicesInRect(impactArea, hGridSize, bullet.direction)
            val hitAnySteels = steelIndices.anyRealElements(mapState.steels)
            if (hitAnySteels) {
                if (bullet.side == TankSide.Player) {
                    soundState.playSound(SoundEffect.HitSteelOrBorder)
                }
                if (bullet.power >= SteelElement.strength) {
                    mapState.destroySteelsIndex(steelIndices.toSet())
                }
            }

            if (impactArea.overlaps(mapState.eagle.rect)) {
                // can be either a direct hit or an AOE impact.
                mapState.destroyEagle()
                soundState.playSound(SoundEffect.ExplosionPlayer)
            }
            // todo check if impact area affects tanks

            when (firstCollision) {
                is HitBorderInfo -> {
                    if (bullet.side == TankSide.Player) {
                        soundState.playSound(SoundEffect.HitSteelOrBorder)
                    }
                }
                is HitTankInfo -> {
                    val afterHit = firstCollision.tank.hitBy(bullet)
                    if (bullet.side == TankSide.Player
                        && firstCollision.tank.side == TankSide.Bot
                        && afterHit.isAlive) {
                        soundState.playSound(SoundEffect.HitArmor)
                    }
                    tankState.hit(bullet, firstCollision.tank)
                }
                is HitBulletInfo -> {

                }
                else -> {

                }
            }
            if (!(firstCollision is HitTankInfo && firstCollision.tank.hasShield)) {
                explosionState.spawnExplosion(firstCollision.impactPoint, ExplosionAnimationSmall)
            }
        }
        removeCollidedBullets(trajectoryCollisionInfo.keys)
        trajectoryCollisionInfo = emptyMap()
    }


    private fun checkCollisionWithBorder() {
        bullets.values.forEach { bullet ->
            if (bullet.x <= 0 || bullet.x >= hGridSize.cell2mpx || bullet.y <= 0 ||
                bullet.y >= vGridSize.cell2mpx) {
                addBulletTrajectoryCollision(HitBorderInfo(bullet, hGridSize, vGridSize))
            }
        }
    }

    private fun checkCollisionWithBullets(tick: Tick) {
        val all = bullets.values.toList()
        for (i in 0 until all.size - 1) {
            val b1 = all[i]
            for (j in i + 1 until all.size) {
                val b2 = all[j]
                if (b1.ownerTankId == b2.ownerTankId) {
                    continue
                }
                if (b1.collisionBox.overlaps(b2.collisionBox)) {
                    val hitRect = b1.collisionBox.intersect(b2.collisionBox)
                    addBulletTrajectoryCollision(HitBulletInfo(bullet = b1, otherBullet = b2, impactPoint = hitRect.center))
                    addBulletTrajectoryCollision(HitBulletInfo(bullet = b2, otherBullet = b1, impactPoint = hitRect.center))
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
                            addBulletTrajectoryCollision(HitBulletInfo(bullet = b1, otherBullet = b2, impactPoint = collisionArea.center))
                            addBulletTrajectoryCollision(HitBulletInfo(bullet = b2, otherBullet = b1, impactPoint = collisionArea.center))
                        }
                    }
                }
            }
        }
    }

    private fun checkCollisionWithBricks(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            BrickElement.getImpactPoint(mapState.bricks, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitBrickInfo(bullet = bullet, impactPoint = it))
            }
        }
    }

    private fun checkCollisionWithSteels(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            SteelElement.getImpactPoint(mapState.steels, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitSteelInfo(bullet = bullet, impactPoint = it))
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
                        addBulletTrajectoryCollision(HitTankInfo(bullet = bullet, impactPoint = impactPoint,tank = tank))
                    }
                }
        }
    }

    private fun checkCollisionWithEagle(tick: Tick) {
        bullets.values.forEach { bullet ->
            val trajectory = bullet.getTrajectory(tick.delta)
            EagleElement.getImpactPoint(mapState.eagle, trajectory, bullet.direction)?.let {
                addBulletTrajectoryCollision(HitEagleInfo(bullet = bullet, impactPoint = it))
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

private fun Bullet.impactPointIfHitBorder(hGridSize: Int, vGridSize: Int): Offset =
    when (direction) {
        Direction.Up -> Offset(center.x, 0f)
        Direction.Down -> Offset(center.x, vGridSize.cell2mpx)
        Direction.Left -> Offset(0f, center.y)
        Direction.Right -> Offset(hGridSize.cell2mpx, center.y)
    }

private sealed class TrajectoryCollisionInfo(open val bullet: Bullet, open val impactPoint: Offset)

private data class HitBorderInfo(override val bullet: Bullet, val hGridSize: Int, val vGridSize: Int) :
    TrajectoryCollisionInfo(bullet, bullet.impactPointIfHitBorder(hGridSize, vGridSize))

private data class HitBrickInfo(override val bullet: Bullet, override val impactPoint: Offset) :
    TrajectoryCollisionInfo(bullet, impactPoint)

private data class HitSteelInfo(override val bullet: Bullet, override val impactPoint: Offset) :
    TrajectoryCollisionInfo(bullet, impactPoint)

private data class HitEagleInfo(override val bullet: Bullet, override val impactPoint: Offset) :
    TrajectoryCollisionInfo(bullet, impactPoint)

private data class HitTankInfo(
    override val bullet: Bullet,
    override val impactPoint: Offset,
    val tank: Tank,
) : TrajectoryCollisionInfo(bullet, impactPoint)

private data class HitBulletInfo(
    override val bullet: Bullet,
    val otherBullet: Bullet,
    override val impactPoint: Offset,
) : TrajectoryCollisionInfo(bullet, impactPoint)