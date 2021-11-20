package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlinx.parcelize.Parcelize
import kotlin.math.max
import kotlin.math.min

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
    val isMoving: Boolean = false,
    val remainingCooldown: Int = 0,
    val timeToSpawn: Int = 0,
) : Parcelable {
    val bulletPower: Int get() = specs.bulletPower
    val bulletSpeed: MapPixel get() = specs.bulletSpeed
    val speed: MapPixel get() = specs.movingSpeed
    val fireCooldown: Int get() = specs.fireCooldown
    val maxBulletCount: Int get() = specs.maxBulletCount
    val isSpawning: Boolean get() = timeToSpawn > 0

    val collisionBox: Rect get() = Rect(Offset(x, y), Size(TANK_MAP_PIXEL, TANK_MAP_PIXEL))
    val offset: Offset get() = Offset(x, y)

    val bulletStartPosition: Offset
        get() =
        when (direction) {
            Direction.Up -> Offset(x + 6, y)
            Direction.Down -> Offset(x + 6, y + 1.grid2mpx)
            Direction.Left -> Offset(x , y + 6)
            Direction.Right -> Offset(x + 1.grid2mpx, y + 6)
        }
}

@Deprecated("delete")
fun Tank.move(distance: MapPixel, turnTo: Direction? = null): Tank {
    var newX = x
    var newY = y
    val newDir = turnTo ?: direction
    when (direction) {
        Direction.Left -> newX -= distance
        Direction.Right -> newX += distance
        Direction.Up -> newY -= distance
        Direction.Down -> newY += distance
    }
    return copy(x = newX, y = newY, direction = newDir, isMoving = distance > 0)
}

fun Tank.moveTo(rect: Rect, newDirection: Direction = direction): Tank =
    copy(x = rect.left, y = rect.top, direction = newDirection, isMoving = rect != collisionBox)

fun Tank.stop(): Tank = copy(isMoving = false)

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
        }.copy(maxHp = 10, bulletPower = 3) // todo revert
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
private val PlayerLevel3Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 2)
private val PlayerLevel4Specs = TankSpecs(maxHp = 2, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 3, fireCooldown = 200, maxBulletCount = 2)
private val BotLevel1Specs = TankSpecs(maxHp = 1, movingSpeed = 0.03f, bulletSpeed = 0.12f, bulletPower = 1, fireCooldown = 300, maxBulletCount = 1)
private val BotLevel2Specs = TankSpecs(maxHp = 1, movingSpeed = 0.06f, bulletSpeed = 0.18f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 1)
private val BotLevel3Specs = TankSpecs(maxHp = 1, movingSpeed = 0.045f, bulletSpeed = 0.24f, bulletPower = 1, fireCooldown = 200, maxBulletCount = 1)
private val BotLevel4Specs = TankSpecs(maxHp = 4, movingSpeed = 0.03f, bulletSpeed = 0.18f, bulletPower = 2, fireCooldown = 200, maxBulletCount = 1)


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