package com.samwdev.battlecity.core

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun rememberTankState(
    explosionState: ExplosionState,
    soundState: SoundState,
    mapState: MapState,
): TankState {
    return remember {
        TankState(soundState, mapState)
    }
}

private val playerSpawnPosition = Offset(4.5f.grid2mpx, 12f.grid2mpx)

class TankState(
    private val soundState: SoundState,
    private val mapState: MapState,
) : TickListener {
    companion object {
        private const val NOT_AN_ID = -1
        // todo to confirm this works as expected
//        fun Saver() = Saver<TankState, Map<TankId, Tank>>(
//            save = { it.tanks },
//            restore = { TankState().apply { tanks = it } }
//        )

    }
    private var nextId = AtomicInteger(0)
    var tanks by mutableStateOf<Map<TankId, Tank>>(mapOf())
        private set

    val aliveTanks: Sequence<Tank> get() = tanks.values.asSequence().filter { it.isAlive }

    var playerTankId: TankId = NOT_AN_ID
        private set
    var whoIsYourDaddy: Boolean = false
        set(value) {
            field = value
            if (playerTankId == NOT_AN_ID) {
                return
            }
            updateTank(playerTankId, getTank(playerTankId).copy(hp = if (value) Int.MAX_VALUE else 1))
        }

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

        if (playerTankId == NOT_AN_ID) {
            spawnPlayer()
        }
    }

    private fun addTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    private fun spawnPlayer(): Tank {
        // todo check remaining life or from last map
        val level = TankLevel.Level4
        return Tank(
            id = nextId.incrementAndGet(),
            x = playerSpawnPosition.x,
            y = playerSpawnPosition.y,
            direction = Direction.Up,
            level = level,
            side = TankSide.Player,
            hp = getTankSpecs(TankSide.Player, level).maxHp,
            timeToSpawn = 500,
        ).also {
            playerTankId = it.id
            addTank(nextId.get(), it)
        }
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
//            Offset(6f.grid2mpx, 6f.grid2mpx),
            Offset(0f.grid2mpx, 0f.grid2mpx),
            Offset(6f.grid2mpx, 0f.grid2mpx),
            Offset(12f.grid2mpx, 0f.grid2mpx),
        ).random()
    }

    fun killTank(tankId: TankId) {
        tanks = tanks.toMutableMap().apply { remove(tankId) }
        if (tankId == playerTankId) {
            playerTankId = NOT_AN_ID
        }
    }

    fun isTankAlive(tankId: TankId):Boolean = tanks[tankId]?.isAlive == true

    fun hit(bullet: Bullet, tank: Tank) {
        // todo bullet from other player
        val updatedTank = tank.hitBy(bullet)
        updateTank(tank.id, updatedTank)

        if (updatedTank.isDead) {
            killTank(tank.id)
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
            updateTank(id, tank.turn(into = direction))
        } else {
            // moving forward, check collision
            val allowedRect = checkCollideIfMoving(tank, distance, tank.direction)
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
        if (id == NOT_AN_ID) {
            return
        }
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    private fun checkCollideIfMoving(tank: Tank, distance: MapPixel, movingDirection: Direction): Rect {
        // todo fix jiggling when moving against other tanks
        val collisionBox = tank.collisionBox
        val toRect = collisionBox.move(distance, movingDirection)
        val travelPath = collisionBox.getTravelPath(toRect)
        val newPivotBox = tank.moveTo(toRect).pivotBox.deflate(1f)

        // check collision against map elements
        val checks = mutableListOf<MoveRestriction>()
        BrickElement.getHitPoint(mapState.bricks, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        SteelElement.getHitPoint(mapState.steels, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        WaterElement.getHitPoint(mapState.waters, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        EagleElement.getHitPoint(mapState.eagle, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }

        // check collision against tanks
        tanks.values.asSequence().filter { it.id != tank.id }.forEach { otherTank ->
            collisionBox.intersect(otherTank.pivotBox)
                .takeIf { !it.isEmpty }
                ?.let { intersect ->
                    checks.add(MoveRestriction(intersect, movingDirection))
                }
        }

        // check collision against map border
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
        return collisionBox.moveUpTo(checks.findMostRestrict())
    }
}