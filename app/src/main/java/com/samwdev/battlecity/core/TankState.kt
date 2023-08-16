package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.IceElement
import com.samwdev.battlecity.entity.PowerUpEnum
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.WaterElement
import java.util.concurrent.atomic.AtomicInteger

class TankState(
    private val gameState: GameState,
    private val soundState: SoundState,
    private val mapState: MapState,
    private val powerUpState: PowerUpState,
    private val explosionState: ExplosionState,
    private val scoreState: ScoreState,
) : TickListener(), GridSizeAware by mapState {
    companion object {
        private const val ShieldDuration = 10 * 1000
        private const val NOT_AN_ID = -1
    }
    private var idGen = AtomicInteger(0)

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
            if (value) {
                updateTank(playerTankId, getTank(playerTankId).copy(remainingShield = Int.MAX_VALUE))
                mapState.fortifyBase(Int.MAX_VALUE)
            } else {
                updateTank(playerTankId, getTank(playerTankId).copy(remainingShield = 0))
                mapState.fortifyBase(-1)
            }
        }

    override fun onTick(tick: Tick) {
        tanks = tanks.mapValues { (_, tank) ->
            val updatedTank = updateTankTimers(tank, tick.delta)
            updateTankMovement(updatedTank, tick.delta)
        }

        if (playerTankId == NOT_AN_ID) {
            spawnPlayer()
        } else {
            checkPowerUpCollision(getTank(playerTankId))
        }
    }

    private fun addTank(id: TankId, tank: Tank) {
        tanks = tanks.toMutableMap().apply {
            put(id, tank)
        }
    }

    private fun spawnPlayer() {
        if (!gameState.deductPlayerLife()) {
            return
        }
        val level = gameState.player1.tankLevel
        Tank(
            id = idGen.incrementAndGet(),
            x = mapState.playerSpawnPosition.x,
            y = mapState.playerSpawnPosition.y,
            facingDirection = Direction.Up,
            level = level,
            side = TankSide.Player,
            hp = getTankSpecs(TankSide.Player, level).maxHp,
            timeToSpawn = 500,
            remainingShield = 3000,
        ).also {
            playerTankId = it.id
            addTank(idGen.get(), it)
        }
    }

    /** May fail due to tank congestion */
    fun spawnBot(level: TankLevel = TankLevel.Level1, hasPowerUp: Boolean = false): Tank? {
        val loc = getAvailableSpawnSpot() ?: return null
        return Tank(
            id = idGen.incrementAndGet(),
            x = loc.x,
            y = loc.y,
            hp = getTankSpecs(TankSide.Bot, level).maxHp,
            level = level,
            facingDirection = Direction.Down,
            side = TankSide.Bot,
            hasPowerUp = hasPowerUp,
            timeToSpawn = 1500,
        ).also {
            addTank(idGen.get(), it)
            mapState.deductRemainingBot()
        }
    }

    private fun getAvailableSpawnSpot(): Offset? {
        val allTanksRect = tanks.values.map { Rect(it.offset, Size(1.cell2mpx, 1.cell2mpx)) }
        val allSpawnRect = mapState.botSpawnPositions.map { Rect(it, Size(1.cell2mpx, 1.cell2mpx)) }.shuffled()
        // find a spawn spot that doesn't collide with any tanks
        return allSpawnRect.find { spawn -> allTanksRect.none { tank -> tank.overlaps(spawn) } }?.topLeft
    }

    fun killTank(tankId: TankId) {
        tanks = tanks.toMutableMap().apply { remove(tankId) }
        if (tankId == playerTankId) {
            playerTankId = NOT_AN_ID
        }
    }

    private fun killAllBots(explosion: Boolean = true) {
        val killedBots = tanks.values.filter { it.side == TankSide.Bot && !it.isSpawning }.map {
            if (explosion) {
                explosionState.spawnExplosion(it.center, ExplosionAnimationBig)
            }
            it.id
        }.toSet()
        if (killedBots.isNotEmpty()) {
            soundState.playSound(SoundEffect.ExplosionBot)
        }
        tanks = tanks.filter { it.key !in killedBots }
    }

    fun isTankAlive(tankId: TankId):Boolean = tanks[tankId]?.isAlive == true

    fun hit(bullet: Bullet, tank: Tank) {
        // todo bullet from other player
        if (tank.hasShield) return

        if (tank.hasPowerUp) {
            powerUpState.spawnPowerUp()
            soundState.playSound(SoundEffect.SpawnPowerUp)
        }

        val updatedTank = tank.hitBy(bullet)
        updateTank(tank.id, updatedTank)

        if (updatedTank.isDead) {
            explosionState.spawnExplosion(tank.center, ExplosionAnimationBig)
            if (tank.side == TankSide.Bot) {
                soundState.playSound(SoundEffect.ExplosionBot)
                scoreState.kill(tank.level, tank.offset)
            } else {
                soundState.playSound(SoundEffect.ExplosionPlayer)
            }
            killTank(tank.id)
        }
    }

    fun getTankOrNull(id: TankId): Tank? {
        return tanks[id]
    }

    fun getTank(id: TankId): Tank {
        return tanks.getValue(id)
    }

    fun getPlayerTankOrNull(): Tank? = getTankOrNull(playerTankId)

    fun moveTank(tankId: TankId, direction: Direction) {
        updateTank(tankId, getTank(tankId).tryMove(dir = direction))
    }

    fun stopTank(tankId: TankId) {
        updateTank(tankId, getTank(tankId).releaseGas())
    }

    private fun updateTankTimers(tank: Tank, delta: Int): Tank {
        var remainingCooldown = tank.remainingCooldown
        var timeToSpawn = tank.timeToSpawn
        var remainingShield = tank.remainingShield
        if (remainingCooldown > 0) {
            remainingCooldown -= delta
        }
        if (timeToSpawn > 0) {
            timeToSpawn -= delta
        }
        if (remainingShield > 0) {
            remainingShield -= delta
        }
        return tank.copy(
            remainingCooldown = remainingCooldown,
            remainingShield = remainingShield,
            timeToSpawn = timeToSpawn,
        )
    }

    private fun updateTankMovement(tank: Tank, delta: Int): Tank {
        var newTank = tank
        val isOnIce = isTankFullOnIce(newTank)
        val acceleration = if (isOnIce) 0.0002f * delta else Float.MAX_VALUE
        if (newTank.isGasPedalPressed) {
            if (isOnIce) {
                newTank = if (newTank.currentSpeed == 0f || newTank.facingDirection == newTank.movingDirection) {
                    // start from stop or already moving in that direction
                    newTank.speedUp(acceleration, maxSpeed = tank.maxSpeed * 0.8f) // slower max speed on ice
                } else if (newTank.facingDirection.isPerpendicularWith(newTank.movingDirection)) {
                    // can't do drifting due to the turning mechanism by pivot box, so straight turn and move
                    newTank.turnAndMove(newTank.facingDirection)
                } else {
                    // facing opposite to the moving direction, speed down to zero first.
                    // Because it tries hard to move, the deceleration is doubled.
                    newTank.speedDown(acceleration * 2)
                }
            } else {
                if (newTank.movingDirection != newTank.facingDirection) {
                    // return in order to take a full tick to make the turn
                    return newTank.turnAndMove(dir = newTank.facingDirection)
                }
                newTank = newTank.speedUp(acceleration)
            }
        } else {
            newTank = newTank.speedDown(acceleration)
        }
        val distance = newTank.currentSpeed * delta
        val allowedRect = checkCollisionIfMoving(newTank, distance, newTank.movingDirection)
        return newTank.moveTo(rect = allowedRect)
    }

    private fun isTankFullOnIce(tank: Tank): Boolean {
        val iceIndices = IceElement.getOverlapIndicesInRect(tank.pivotBox, mapState.hGridSize)
        val allIceIndices = mapState.iceIndexSet
        return iceIndices.all { it in allIceIndices }
    }

    fun startFireCooldown(id: TankId) {
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

    private fun checkCollisionIfMoving(tank: Tank, distance: MapPixel, movingDirection: Direction): Rect {
        val collisionBox = tank.collisionBox
        val toRect = collisionBox.move(distance, movingDirection)
        val travelPath = collisionBox.getTravelPath(toRect)

        // check collision against map elements
        val checks = mutableListOf<MoveRestriction>()
        BrickElement.getImpactPoint(mapState.bricks, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        SteelElement.getImpactPoint(mapState.steels, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        WaterElement.getImpactPoint(mapState.waters, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }
        EagleElement.getImpactPoint(mapState.eagle, travelPath, movingDirection)
            ?.also { checks.add(MoveRestriction(it, movingDirection)) }

        // check collision against tanks
        tanks.values.asSequence().filter { it.id != tank.id }.forEach { otherTank ->
            travelPath.intersect(otherTank.pivotBox)
                .takeIf { !it.isEmpty }
                ?.let { intersect ->
                    checks.add(MoveRestriction(intersect, movingDirection))
                }
        }

        // check collision against map border
        val mapRect = Rect(offset = Offset.Zero, size = Size(hGridSize.cell2mpx, vGridSize.cell2mpx))
        if (mapRect.intersect(toRect) != toRect) {
            // going out of bound
            val boundary = when (movingDirection) {
                Direction.Up -> 0f
                Direction.Down -> vGridSize.cell2mpx
                Direction.Left -> 0f
                Direction.Right -> hGridSize.cell2mpx
            }
            checks.add(MoveRestriction(boundary, movingDirection))
        }

        if (checks.isEmpty()) {
            // would collide with nothing
            return toRect
        }
        return collisionBox.moveUpTo(checks.getMostRestricted())
    }

    private fun checkPowerUpCollision(tank: Tank) {
        if (powerUpState.powerUps.isEmpty()) {
            return
        }
        val toPickUp = powerUpState.powerUps.values.filter { p -> p.rect.overlaps(tank.collisionBox) }
        powerUpState.remove(toPickUp)
        toPickUp.forEach { pickUpPowerUp(tank, it) }
    }

    private fun pickUpPowerUp(tank: Tank, powerUp: PowerUp) {
        scoreState.pickUpPowerUp(powerUp.offset)
        when (powerUp.type) {
            PowerUpEnum.Helmet -> {
                updateTank(tank.id, tank.shieldOn(ShieldDuration))
                soundState.playSound(SoundEffect.PickUpPowerUp)
            }
            PowerUpEnum.Star -> {
                updateTank(tank.id, tank.levelUp())
                soundState.playSound(SoundEffect.PickUpPowerUp)
                gameState.levelUp()
            }
            PowerUpEnum.Grenade -> {
                killAllBots()
                soundState.playSound(SoundEffect.PickUpPowerUp)
            }
            PowerUpEnum.Tank -> {
                gameState.addPlayerLife()
                soundState.playSound(SoundEffect.PickUpLifePowerUp)
            }
            PowerUpEnum.Shovel -> {
                fortifyBase()
                soundState.playSound(SoundEffect.PickUpPowerUp)
            }
            PowerUpEnum.Timer -> {
                mapState.freezeBots()
                soundState.playSound(SoundEffect.PickUpPowerUp)
            }
        }
    }

    private fun fortifyBase() {
        mapState.fortifyBase()
    }
}

// todo
sealed class TankEvent
data class MoveTankEvent(val tankId: TankId, val direction: Direction) : TankEvent()
data class StopTankEvent(val tankId: TankId) : TankEvent()
data class SpawnBotEvent(val level: TankLevel, val hasPowerUp: Boolean) : TankEvent()
data class KillTankEvent(val tankId: TankId) : TankEvent()
object SmokeAllBotsEvent : TankEvent()
data class HitTankEvent(val bullet: Bullet, val tankId: TankId) : TankEvent()
data class PickUpPowerUpEvent(val tankId: TankId, val powerUpEnum: PowerUpEnum) : TankEvent()
