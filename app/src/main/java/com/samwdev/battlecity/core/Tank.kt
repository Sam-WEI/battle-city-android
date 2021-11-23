package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.PowerUpEnum
import kotlinx.parcelize.Parcelize
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

typealias TankId = Int

@Parcelize
data class Tank(
    val id: TankId,
    val x: MapPixel = 0f,
    val y: MapPixel = 0f,
    val direction: Direction = Direction.Up,
    val level: TankLevel = TankLevel.Level1,
    val side: TankSide,
    val hp: Int,
    val hasPowerUp: Boolean = false,
    val remainingShield: Int = 0,
    val remainingCooldown: Int = 0,
    val timeToSpawn: Int = 0,
) : Parcelable {
    val offset: Offset get() = Offset(x, y)
    val bulletPower: Int get() = specs.bulletPower
    val bulletSpeed: MapPixel get() = specs.bulletSpeed
    val speed: MapPixel get() = specs.movingSpeed
    val fireCooldown: Int get() = specs.fireCooldown
    val maxBulletCount: Int get() = specs.maxBulletCount
    val isSpawning: Boolean get() = timeToSpawn > 0
    val isAlive: Boolean get() = hp > 0
    val isDead: Boolean get() = !isAlive
    val hasShield: Boolean get() = remainingShield > 0

    val collisionBox: Rect get() = Rect(Offset(x, y), Size(TANK_MAP_PIXEL, TANK_MAP_PIXEL))
    val pivotBox: Rect get() {
        val halfBlock = 0.5f.grid2mpx.toInt() // 8
        val pbx = (x / halfBlock).roundToInt() * halfBlock.toFloat()
        val pby = (y / halfBlock).roundToInt() * halfBlock.toFloat()
        return Rect(Offset(pbx, pby), Size(TANK_MAP_PIXEL, TANK_MAP_PIXEL))
    }

    val bulletStartPosition: Offset
        get() =
        when (direction) {
            Direction.Up -> Offset(x + 6, y)
            Direction.Down -> Offset(x + 6, y + 1.grid2mpx)
            Direction.Left -> Offset(x , y + 6)
            Direction.Right -> Offset(x + 1.grid2mpx, y + 6)
        }
}

fun Tank.turn(into: Direction): Tank {
    if (into == direction) { return this }
    val newX = if (into.isVertical()) pivotBox.left else x
    val newY = if (into.isVertical()) y else pivotBox.top
    return copy(direction = into, x = newX, y = newY)
}

fun Tank.moveTo(rect: Rect, newDirection: Direction = direction): Tank =
    copy(x = rect.left, y = rect.top, direction = newDirection)

fun Tank.hitBy(bullet: Bullet): Tank {
    return copy(hp = hp - 1, hasPowerUp = false)
}

enum class Direction(val degree: Float) {
    Up(0f),
    Down(180f),
    Left(270f),
    Right(90f);

    fun isVertical(): Boolean = this == Up || this == Down
    fun isHorizontal(): Boolean = this == Left || this == Right
}

enum class TankSide {
    Player, Bot,
}

enum class TankLevel {
    Level1, Level2, Level3, Level4
}

private val Tank.specs: TankSpecs get() = getTankSpecs(side, level)

fun getTankSpecs(side: TankSide, level: TankLevel) =
    if (side == TankSide.Player) {
        when (level) {
            TankLevel.Level1 -> PlayerLevel1Specs
            TankLevel.Level2 -> PlayerLevel2Specs
            TankLevel.Level3 -> PlayerLevel3Specs
            TankLevel.Level4 -> PlayerLevel4Specs
        }.copy(maxHp = 2) // todo revert
    } else {
        when (level) {
            TankLevel.Level1 -> BotLevel1Specs
            TankLevel.Level2 -> BotLevel2Specs
            TankLevel.Level3 -> BotLevel3Specs
            TankLevel.Level4 -> BotLevel4Specs
        }
    }

data class TankSpecs(
    val maxHp: Int,
    val movingSpeed: MapPixel,
    val bulletSpeed: MapPixel,
    val bulletPower: Int,
    val fireCooldown: Int,
    val maxBulletCount: Int,
)

private val PlayerLevel1Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.12f, bulletPower = 1, fireCooldown = 300, maxBulletCount = 1)
private val PlayerLevel2Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 1)
private val PlayerLevel3Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 1, fireCooldown = 100, maxBulletCount = 2)
private val PlayerLevel4Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 3, fireCooldown = 100, maxBulletCount = 2)
private val BotLevel1Specs = TankSpecs(maxHp = 1, movingSpeed = 0.03f, bulletSpeed = 0.12f, bulletPower = 1, fireCooldown = 300, maxBulletCount = 1)
private val BotLevel2Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 1)
private val BotLevel3Specs = TankSpecs(maxHp = 1, movingSpeed = 0.045f, bulletSpeed = 0.24f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 1)
private val BotLevel4Specs = TankSpecs(maxHp = 4, movingSpeed = 0.045f, bulletSpeed = 0.18f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 1)


fun Rect.getTravelPath(to: Rect): Rect {
    return Rect(
        left = min(left, to.left),
        top = min(top, to.top),
        right = max(right, to.right),
        bottom = max(bottom, to.bottom),
    )
}

fun Rect.move(distance: MapPixel, direction: Direction): Rect =
    when (direction) {
        Direction.Up -> translate(0f, -distance)
        Direction.Down -> translate(0f, distance)
        Direction.Left -> translate(-distance, 0f)
        Direction.Right -> translate(distance, 0f)
    }

fun Rect.moveUpTo(moveRestriction: MoveRestriction): Rect {
    return when (moveRestriction.direction) {
        Direction.Up -> Rect(offset = Offset(left, moveRestriction.bound), size = size)
        Direction.Down -> Rect(offset = Offset(left, moveRestriction.bound - height), size = size)
        Direction.Left -> Rect(offset = Offset(moveRestriction.bound, top), size = size)
        Direction.Right -> Rect(offset = Offset(moveRestriction.bound - width, top), size = size)
    }
}

data class MoveRestriction(val bound: MapPixel, val direction: Direction) {
    constructor(hitPoint: Offset, direction: Direction) :
            this (if (direction.isVertical()) hitPoint.y else hitPoint.x, direction)
    constructor(hitRect: Rect, direction: Direction) :
            this (
                when (direction) {
                     Direction.Up -> hitRect.bottom
                     Direction.Down -> hitRect.top
                     Direction.Left -> hitRect.right
                     Direction.Right -> hitRect.left
                 },
                direction
            )
}

fun List<MoveRestriction>.findMostRestrict(): MoveRestriction {
    require(isNotEmpty())
    val mostRestrict = when (first().direction) {
        Direction.Up, Direction.Left -> this.maxOf { it.bound }
        Direction.Down, Direction.Right -> this.minOf { it.bound }
    }
    return MoveRestriction(mostRestrict, first().direction)
}