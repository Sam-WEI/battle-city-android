package com.samwdev.battlecity.core

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BotTankLevel
import kotlinx.parcelize.Parcelize
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

@Composable
fun rememberTankState(
    explosionState: ExplosionState,
    soundState: SoundState,
): TankState {
    return remember {
        TankState(explosionState, soundState)
    }
}

//private val playerSpawnPosition = Offset(4.5f.grid2mpx, 12f.grid2mpx)
private val playerSpawnPosition = Offset(12f.grid2mpx, 4f.grid2mpx)

class TankState(
    private val explosionState: ExplosionState,
    private val soundState: SoundState,
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
        tanks = tanks.keys.associateWith { id ->
            val tank = tanks[id]!!
            return@associateWith if (tank.remainingCooldown > 0) {
                tank.copy(remainingCooldown = tank.remainingCooldown - tick.delta.toInt())
            } else {
                tank
            }
        }
    }

    private fun addTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    fun spawnPlayer(): Tank {
        return Tank(
            id = nextId.incrementAndGet(),
            x = playerSpawnPosition.x,
            y = playerSpawnPosition.y,
            direction = Direction.Up,
        ).also { addTank(nextId.get(), it) }
    }

    fun spawnBot(): Tank {
        val loc = getRandomSpawnLocation()
        return Tank(
            id = nextId.incrementAndGet(),
            x = loc.x,
            y = loc.y,
            direction = Direction.Right,
            side = TankSide.Bot,
        ).also { addTank(nextId.get(), it) }
    }

    private fun getRandomSpawnLocation(): Offset {
        return listOf(
            Offset(0f.grid2mpx, 4f.grid2mpx),
            Offset(0f.grid2mpx, 0f.grid2mpx),
            Offset(6f.grid2mpx, 0f.grid2mpx),
            Offset(12f.grid2mpx, 0f.grid2mpx),
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

    fun getTank(id: TankId): Tank? {
        return tanks[id]
    }

    fun updateTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    fun startCooldown(id: TankId) {
        val tank = tanks[id]!!
        updateTank(id, tank.copy(remainingCooldown = tank.getFireCooldown()))
    }
}

typealias TankId = Int

@Parcelize
data class Tank(
    val id: TankId,
    val x: MapPixel = 0f,
    val y: MapPixel = 0f,
    val remainingCooldown: Int = 0,
    val direction: Direction = Direction.Up,
    val level: BotTankLevel = BotTankLevel.Basic,
    val hp: Int = 1,
    val speed: MapPixel = 0.05f,
    val side: TankSide = TankSide.Player,
    val isMoving: Boolean = false,
) : Parcelable {
    val bulletPower: Int get() = if (side == TankSide.Player) 3 else 1
    val collisionBox: Rect get() = Rect(Offset(x, y), Size(TANK_MAP_PIXEL, TANK_MAP_PIXEL))

    fun getBulletStartPosition(): Offset {
        return when (direction) {
            Direction.Up -> Offset(x + 6, y)
            Direction.Down -> Offset(x + 6, y + 1.grid2mpx)
            Direction.Left -> Offset(x , y + 6)
            Direction.Right -> Offset(x + 1.grid2mpx, y + 6)
        }
    }

    fun getFireCooldown(): Int = level.fireCooldown

    fun getMaxBulletLimit(): Int = if (side == TankSide.Player) 3 else 1
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
    Player,
    Bot,
}
