package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement
import com.samwdev.battlecity.utils.*
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun rememberTankState(
    explosionState: ExplosionState,
    soundState: SoundState,
    mapState: MapState,
): TankState {
    return remember {
        TankState(explosionState, soundState, mapState)
    }
}

//private val playerSpawnPosition = Offset(4.5f.grid2mpx, 12f.grid2mpx)
private val playerSpawnPosition = Offset(12f.grid2mpx, 4f.grid2mpx)

class TankState(
    private val explosionState: ExplosionState,
    private val soundState: SoundState,
    private val mapState: MapState,
) : TickListener {
    companion object {
        // todo to confirm this works as expected
//        fun Saver() = Saver<TankState, Map<TankId, Tank>>(
//            save = { it.tanks },
//            restore = { TankState().apply { tanks = it } }
//        )

    }
    var tanks by mutableStateOf<Map<TankId, Tank>>(mapOf())
        private set

    private var nextId = AtomicInteger(0)

    override fun onTick(tick: Tick) {
        val newTanks: MutableMap<TankId, Tank> = mutableMapOf()
        tanks.forEach { (id, tank) ->
            var remainingCooldown = tank.remainingCooldown
            var timeToSpawn = tank.timeToSpawn
            if (remainingCooldown > 0) {
                remainingCooldown -= tick.delta.toInt()
            }
            if (timeToSpawn > 0) {
                timeToSpawn -= tick.delta.toInt()
            }
            val newTank = tank.copy(
                remainingCooldown = remainingCooldown,
                timeToSpawn = timeToSpawn,
            )
            newTanks[id] = newTank
        }
        tanks = newTanks
    }

    private fun addTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    fun spawnPlayer(level: TankLevel = TankLevel.Level1): Tank {
        return Tank(
            id = nextId.incrementAndGet(),
            x = playerSpawnPosition.x,
            y = playerSpawnPosition.y,
            direction = Direction.Up,
            level = level,
            side = TankSide.Player,
            hp = getTankSpecs(TankSide.Player, level).maxHp,
            timeToSpawn = 500,
        ).also { addTank(nextId.get(), it) }
    }

    fun spawnBot(level: TankLevel = TankLevel.Level1): Tank {
        val loc = getRandomSpawnLocation()
        return Tank(
            id = nextId.incrementAndGet(),
            x = loc.x,
            y = loc.y,
            hp = getTankSpecs(TankSide.Bot, level).maxHp,
            level = level,
            direction = Direction.Right,
            side = TankSide.Bot,
            timeToSpawn = 1500,
        ).also { addTank(nextId.get(), it) }
    }

    private fun getRandomSpawnLocation(): Offset {
        return listOf(
            Offset(0f.grid2mpx, 4f.grid2mpx),
            Offset(0f.grid2mpx, 0f.grid2mpx),
//            Offset(6f.grid2mpx, 0f.grid2mpx),
//            Offset(12f.grid2mpx, 0f.grid2mpx),
        ).random()
    }

    fun killTank(tankId: TankId) {
        tanks = tanks.toMutableMap().apply { remove(tankId) }
    }

    fun isTankAlive(tankId: TankId):Boolean = tanks.any { it.key == tankId }

    fun hit(bullet: Bullet, tank: Tank) {
        // todo bullet from other player
        val newHp = tank.hp - bullet.power
        if (newHp <= 0) {
            explosionState.spawnExplosion(tank.collisionBox.center, ExplosionAnimationBig)
            soundState.playSound(SoundEffect.Explosion1)
            killTank(tank.id)
        } else {
            tanks = tanks.toMutableMap().apply {
                put(tank.id, tank.copy(hp = newHp))
            }
        }
    }

    fun getTankOrNull(id: TankId): Tank? {
        return tanks[id]
    }

    fun getTank(id: TankId): Tank {
        return tanks.getValue(id)
    }

    fun moveTank(id: TankId, direction: Direction, distance: MapPixel) {
        val tank = getTank(id)
        if (tank.direction != direction) {

            updateTank(id, tank.copy(direction = direction))
        } else {
            // moving forward, check collision
            val allowedRect = checkCollideWithBricks(tank.collisionBox, distance, tank.direction)
            updateTank(id, tank.moveTo(rect = allowedRect))
        }
    }

    fun stopTank(id: TankId) {
        val tank = getTank(id)
        updateTank(id, tank.stop())
    }

    fun startCooldown(id: TankId) {
        val tank = tanks.getValue(id)
        updateTank(id, tank.copy(remainingCooldown = tank.fireCooldown))
    }

    private fun updateTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    private fun checkCollideWithBricks(from: Rect, distance: MapPixel, movingDirection: Direction): Rect {
        // todo fix jiggling when moving right and down
        val toRect = from.move(distance, movingDirection)
        val travelPath = from.getTravelPath(toRect)

        val checks = mutableListOf<MoveRestriction>()
        BrickElement.getHitPoint(mapState.bricks, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        SteelElement.getHitPoint(mapState.steels, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        WaterElement.getHitPoint(mapState.waters, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        EagleElement.getHitPoint(mapState.eagle, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }

        val mapRect = Rect(offset = Offset.Zero, size = Size(MAP_BLOCK_COUNT.grid2mpx, MAP_BLOCK_COUNT.grid2mpx))
        if (mapRect.intersect(toRect) != toRect) {
            // going out of bound
            val boundary = when (movingDirection) {
                Direction.Up -> 0f
                Direction.Down -> MAP_BLOCK_COUNT.grid2mpx
                Direction.Left -> 0f
                Direction.Right -> MAP_BLOCK_COUNT.grid2mpx
            }
            checks.add(MoveRestriction(boundary, movingDirection))
        }

        if (checks.isEmpty()) {
            // would collide with nothing
            return toRect
        }
        return from.moveUpTo(checks.findMostRestrict())
    }

}

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

    val bulletStartPosition: Offset get() =
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

private fun getTankSpecs(side: TankSide, level: TankLevel) =
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

private data class TankSpecs(
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