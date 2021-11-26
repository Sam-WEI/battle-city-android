package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlinx.parcelize.Parcelize
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

typealias TankId = Int

@Parcelize
data class Tank(
    val id: TankId,
    val x: MapPixel = 0f,
    val y: MapPixel = 0f,
    val movingDirection: Direction = Direction.Up,
    val facingDirection: Direction = Direction.Up,
    val level: TankLevel = TankLevel.Level1,
    val side: TankSide,
    val hp: Int,
    val currentSpeed: MapPixel = 0f,
    val isGasPedalPressed: Boolean = false,
    val hasPowerUp: Boolean = false,
    val remainingShield: Int = 0,
    val remainingCooldown: Int = 0,
    val timeToSpawn: Int = 0,
    val isSliding: Boolean = false,
) : Parcelable {
    val offset: Offset get() = Offset(x, y)
    val bulletPower: Int get() = specs.bulletPower
    val bulletSpeed: MapPixel get() = specs.bulletSpeed
    val maxSpeed: MapPixel get() = specs.movingSpeed
    val isMoving: Boolean get() = currentSpeed > 0
    val fireCooldown: Int get() = specs.fireCooldown
    val maxBulletCount: Int get() = specs.maxBulletCount
    val isSpawning: Boolean get() = timeToSpawn > 0
    val isAlive: Boolean get() = hp > 0
    val isDead: Boolean get() = !isAlive
    val hasShield: Boolean get() = remainingShield > 0
    val collisionBox: Rect get() = Rect(Offset(x, y), Size(TANK_MAP_PIXEL, TANK_MAP_PIXEL))
    val center: Offset get() = collisionBox.center
    val pivotBox: Rect get() {
        val halfBlock = 0.5f.grid2mpx.toInt() // 8
        // this tiny offset is to fix collision with brick quarters
        val tinyOffset = if (movingDirection == Direction.Right || movingDirection == Direction.Down) { 0.1f } else { 0f }
        val pbx = (x / halfBlock - tinyOffset).roundToInt() * halfBlock.toFloat()
        val pby = (y / halfBlock - tinyOffset).roundToInt() * halfBlock.toFloat()
        return Rect(Offset(pbx, pby), Size(TANK_MAP_PIXEL, TANK_MAP_PIXEL))
    }

    val bulletStartPosition: Offset
        get() = when (facingDirection) {
            Direction.Up -> Offset(x + 6, y)
            Direction.Down -> Offset(x + 6, y + 1.grid2mpx)
            Direction.Left -> Offset(x , y + 6)
            Direction.Right -> Offset(x + 1.grid2mpx, y + 6)
        }
}

/** face the direction, but not necessarily able to move into this direction (sliding on ice) */
fun Tank.tryMove(dir: Direction): Tank = copy(facingDirection = dir, isGasPedalPressed = true)

fun Tank.turnAndMove(dir: Direction): Tank {
    if (dir == movingDirection && dir == facingDirection) { return this }
    val newX = if (dir.isVertical) pivotBox.left else x
    val newY = if (dir.isVertical) y else pivotBox.top
    return copy(movingDirection = dir, facingDirection = dir, x = newX, y = newY, isGasPedalPressed = true)
}

fun Tank.moveTo(rect: Rect, newDirection: Direction = movingDirection): Tank =
    copy(x = rect.left, y = rect.top, movingDirection = newDirection)

fun Tank.speedUp(acceleration: Float): Tank {
    return copy(
        currentSpeed = (currentSpeed + acceleration).coerceAtMost(maxSpeed),
        movingDirection = facingDirection,
        isGasPedalPressed = true,
    )
}

fun Tank.speedDown(deceleration: Float): Tank {
    return copy(currentSpeed = (currentSpeed - deceleration).coerceAtLeast(0f))
}

fun Tank.releaseGas(): Tank = copy(isGasPedalPressed = false)

fun Tank.hitBy(bullet: Bullet): Tank {
    if (hasShield) return this
    return copy(hp = hp - 1, hasPowerUp = false)
}

fun Tank.levelUp(): Tank = copy(level = this.level.nextLevel)

fun Tank.shieldOn(duration: Int): Tank = copy(remainingShield = duration)

enum class Direction(val degree: Int) {
    Up(0),
    Down(180),
    Left(270),
    Right(90);

    val opposite: Direction get() = when (this) {
        Up -> Down
        Down -> Up
        Left -> Right
        Right -> Left
    }
    val isVertical: Boolean get() = this == Up || this == Down
    val isHorizontal: Boolean get() = this == Left || this == Right
    fun isPerpendicularWith(other: Direction): Boolean = isVertical xor other.isVertical
    fun isOppositeTo(other: Direction): Boolean = (degree - other.degree).absoluteValue == 180
}

enum class TankSide {
    Player, Bot,
}

enum class TankLevel {
    Level1, Level2, Level3, Level4;
    val nextLevel: TankLevel get() = when (this) {
        Level1 -> Level2
        Level2 -> Level3
        Level3 -> Level4
        Level4 -> Level4
    }
}

private val Tank.specs: TankSpecs get() = getTankSpecs(side, level)

fun getTankSpecs(side: TankSide, level: TankLevel) =
    if (side == TankSide.Player) {
        when (level) {
            TankLevel.Level1 -> PlayerLevel1Specs
            TankLevel.Level2 -> PlayerLevel2Specs
            TankLevel.Level3 -> PlayerLevel3Specs
            TankLevel.Level4 -> PlayerLevel4Specs
        }
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

/**
 * Returns the rect difference in front of the tank
 */
fun Rect.getTravelPath(to: Rect): Rect {
    val movingDirection = if (to.left != left) {
        if (to.left < left) Direction.Left else Direction.Right
    } else {
        if (to.top < top) Direction.Up else Direction.Down
    }
    return when (movingDirection) {
        Direction.Up -> Rect(left, to.top, right, top)
        Direction.Down -> Rect(left, bottom, right, to.bottom)
        Direction.Left -> Rect(to.left, top, left, bottom)
        Direction.Right -> Rect(right, top, to.right, bottom)
    }
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
            this (if (direction.isVertical) hitPoint.y else hitPoint.x, direction)
    constructor(hitRect: Rect, direction: Direction) :
            this (
                when (direction) {
                     Direction.Up -> hitRect.bottom
                     Direction.Down -> hitRect.top
                     Direction.Left -> hitRect.right
                     Direction.Right -> hitRect.left
                 }.roundToInt().toFloat(),
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