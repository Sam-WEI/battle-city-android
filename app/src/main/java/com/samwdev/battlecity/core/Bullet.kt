package com.samwdev.battlecity.core

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.math.max
import kotlin.math.min

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
        val depth = if (power <= 2) { 1f.cell2mpx / 4 } else { 1f.cell2mpx / 2 }
        val width = 1f.cell2mpx - 1f
        val reflect = 1f.toMpx // the AOE range off of the surface

        return when (direction) {
            Direction.Up -> Rect(
                offset = Offset(hitPoint.x - width / 2 + 1f, hitPoint.y - depth),
                size = Size(width, depth + reflect)
            )
            Direction.Down -> Rect(
                offset = Offset(hitPoint.x - width / 2 + 1f, hitPoint.y - reflect),
                size = Size(width, depth + reflect)
            )
            Direction.Left -> Rect(
                offset = Offset(hitPoint.x - depth, hitPoint.y - width / 2 + 1f),
                size = Size(depth + reflect, width)
            )
            Direction.Right -> Rect(
                offset = Offset(hitPoint.x - reflect, hitPoint.y - width / 2 + 1f),
                size = Size(depth + reflect, width)
            )
        }
    }

    fun getFlashbackBullet(millisAgo: Int): Bullet {
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

    fun getTrajectory(vararg flashbackDeltas: Int): Rect {
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

        return Rect(
            topLeft = Offset(left, top),
            bottomRight = Offset(right, bottom)
        )
    }
}