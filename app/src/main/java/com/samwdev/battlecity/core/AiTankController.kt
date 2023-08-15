package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.samwdev.battlecity.utils.Logger
import kotlin.random.Random

class AiTankController(
    private val tankId: TankId,
    private val tankState: TankState,
    private val bulletState: BulletState, // todo move out?
    private val mapState: MapState,
) : TickListener {
    var currentWaypoint: List<SubGrid> by mutableStateOf(listOf(), referentialEqualityPolicy())
        private set
    private var lastTankPivotTopLeft: Offset? = null
    private var stuckTime: Int = 0
    private var remainingAiDecisionCooldown = 0
    private var remainingFireDecisionCooldown = 0

    private val personality: AiPersonality = AiPersonality()
    private var remainingHuntingPlayerTime: Int = 0
    private var remainingLockOnPlayerCooldown: Int = 0

    private var failedToFindAPathCount = 0

    init {
        Logger.info("tank[$tankId] personality: ${personality}")
    }

    override fun onTick(tick: Tick) {
        if (!tankState.isTankAlive(tankId)) {
            return
        }
        val tank = tankState.getTank(tankId)
        if (tank.isSpawning) {
            return
        }

        if (mapState.areBotsFrozen) {
            tankState.stopTank(tank.id)
            return
        }

        // making fire decision
        remainingFireDecisionCooldown -= tick.delta
        if (remainingFireDecisionCooldown <= 0) {
            if (Random.nextFloat() < personality.fireChance) {
                bulletState.fire(tank)
            }
            remainingFireDecisionCooldown = personality.fireDecisionCooldown
        }

        // todo after frozen, continue prev waypoints

        remainingLockOnPlayerCooldown -= tick.delta
        remainingHuntingPlayerTime -= tick.delta

        if (remainingHuntingPlayerTime > 0) {
            if (remainingLockOnPlayerCooldown <= 0) {
                findNewWaypoint(tank, tankState.getPlayerTankOrNull()?.offset?.subGrid)
                remainingLockOnPlayerCooldown = personality.lockOnPlayerCooldown
                Logger.debug("[${tankId}] re-locked player!! remaining hunting time: $remainingHuntingPlayerTime")
            }
        }

        if (currentWaypoint.isNotEmpty()) {
            // move along waypoints
            val currTankSubGrid = tank.pivotBox.topLeft.subGrid
            val currWaypoint = currentWaypoint.first()
            if (currWaypoint == currTankSubGrid) {
                currentWaypoint = currentWaypoint.subList(1, currentWaypoint.size)
                currentWaypoint.firstOrNull()?.let { nextWayPoint ->
                    val dir = Direction.values().find { currWaypoint.getNeighborInDirection(it) == nextWayPoint }!!
                    tankState.moveTank(tank.id, dir)
                }
            } else {
                if (lastTankPivotTopLeft != tank.pivotBox.topLeft) {
                    lastTankPivotTopLeft = tank.pivotBox.topLeft
                    stuckTime = 0
                } else {
                    stuckTime += tick.delta
                    if (stuckTime > personality.stuckTimeout) {
                        findNewWaypoint(tank, null)
                        stuckTime = 0
                    }
                }
            }
        } else {
            // done with prev waypoints, finding new ones
            tankState.stopTank(tank.id)
            if (remainingAiDecisionCooldown > 0) {
                remainingAiDecisionCooldown -= tick.delta
                return
            }
            remainingAiDecisionCooldown = personality.aiDecisionCooldown
            when (Random.nextFloat()) {
                in 0f..personality.attackPlayer -> {
                    findNewWaypoint(tank, tankState.getPlayerTankOrNull()?.offset?.subGrid)
                    remainingHuntingPlayerTime = personality.huntingPlayerDuration
                    remainingLockOnPlayerCooldown = personality.lockOnPlayerCooldown
                    Logger.info("[${tankId}] Locked player!! Hunting time: $remainingHuntingPlayerTime")

                }
                in personality.attackPlayer..(personality.attackPlayer + personality.attackBase) -> {
                    findNewWaypoint(tank, mapState.eagle.offsetInMapPixel.subGrid, attackingBase = true)
                    Logger.info("[${tankId}] Locked base!!")
                }
                else -> {
                    findNewWaypoint(tank, null)
                    Logger.info("[${tankId}] Go shopping.")
                }
            }
        }
    }

    private fun findNewWaypoint(tank: Tank, target: SubGrid?, attackingBase: Boolean = false) {
        val accessPoints = mapState.accessPoints
        val dest = target ?: accessPoints.randomAccessiblePoint(Int.MAX_VALUE)
        val src = tank.pivotBox.topLeft.subGrid
        val waypoints = LinkedHashSet<SubGrid>().apply { add(src) }
        walkWaypointRecursive(
            waypoints,
            mutableSetOf(),
            accessPoints,
            src,
            dest,
            null,
            breakingBricks = failedToFindAPathCount >= personality.breakingBrickThreshold,
            attackingBase = attackingBase,
        )
        currentWaypoint = waypoints.toList()
        if (waypoints.size == 1) {
            // failed to find a path
            Logger.info("tank[$tankId] failed to find a path.")
            failedToFindAPathCount++
        } else {
//            failedToFindAPathCount = 0
        }
    }

    private fun walkWaypointRecursive(
        waypoints: LinkedHashSet<SubGrid>,
        badAccessPoints: MutableSet<SubGrid>,
        accessPoints: AccessPoints,
        src: SubGrid,
        dest: SubGrid,
        currentDirection: Direction?,
        breakingBricks: Boolean = false,
        attackingBase: Boolean = false,
    ): Boolean {
        if (src == dest) {
            return true
        }
        val directionAttemptPriority = mutableListOf<Direction>()
        if (dest.subRow != src.subRow && dest.subCol != src.subCol) {
            // prefer going vertically
            directionAttemptPriority.add(if (dest.subRow > src.subRow) Direction.Down else Direction.Up)
            directionAttemptPriority.add(if (dest.subCol > src.subCol) Direction.Right else Direction.Left)
        } else if (dest.subRow == src.subRow) {
            // horizontally equal, go vertically first
            directionAttemptPriority.add(if (dest.subCol > src.subCol) Direction.Right else Direction.Left)
        } else if (dest.subCol == src.subCol) {
            // vertically equal, go horizontally first
            directionAttemptPriority.add(if (dest.subRow > src.subRow) Direction.Down else Direction.Up)
        }
        // then, prefer continuing the current direction
        currentDirection?.let { directionAttemptPriority.add(currentDirection) }
        // then randomly choose another direction
        Direction.values().asSequence()
            .shuffled()
            .filter { it != currentDirection?.opposite } // never go back
            .forEach {
                if (it !in directionAttemptPriority) {
                    directionAttemptPriority.add(it)
                }
            }
        for (dir in directionAttemptPriority) {
            val nextWp = src.getNeighborInDirection(dir)
            if (nextWp.isOutOfBound(accessPoints)) {
                continue
            }
            val canBreak = breakingBricks && accessPoints.isBrick(nextWp)
            val canAttack = attackingBase && accessPoints.isEagleArea(nextWp)
            if (!accessPoints.isAccessible(nextWp)
                && !canBreak
                && !canAttack) {
                continue
            }
            if (nextWp in badAccessPoints) {
                continue
            }
            if (nextWp in waypoints) {
                // circled
                continue
            }
            waypoints.add(nextWp)
            val reached = walkWaypointRecursive(
                waypoints,
                badAccessPoints,
                accessPoints,
                nextWp,
                dest,
                dir,
                breakingBricks = breakingBricks,
                attackingBase = attackingBase,
            )
            if (reached) {
                return true
            } else {
                waypoints.remove(nextWp)
                badAccessPoints.add(nextWp)
            }
        }
        return false
    }
}

private sealed class WaypointMode
private object AttackPlayer : WaypointMode()
private object Wander : WaypointMode()
private object AttackBase : WaypointMode()

private class AiPersonality(difficulty: Int = 1) {
    // fire frequency, invasion frequency
    private val maniac: Float = (Random.nextInt(3) + difficulty) / 5f

    private val aggressiveTowardsPlayer: Float = (Random.nextInt(3) + difficulty) / 5f

    private val aggressiveTowardsBase: Float = (Random.nextInt(3) + difficulty) / 5f

    // decision making cooldown
    private val wisdom: Float = (Random.nextInt(3) + difficulty) / 5f

    // when stuck, agile AI waits shorter before changing path
    private val agility: Float = (Random.nextInt(3) + difficulty) / 5f

    // derived attributes below
    val fireDecisionCooldown: Int get() = (70 / maniac).toInt()
    val fireChance: Float get() = 0.2f * maniac
    val attackPlayer: Float get() = 0.1f * aggressiveTowardsPlayer
    // when decided to hunt player, it takes this long before giving up.
    val huntingPlayerDuration: Int get() = (7 * 1000 * aggressiveTowardsPlayer).toInt()
    // re-lock on player because player keeps moving
    val lockOnPlayerCooldown: Int get() = (1000 / aggressiveTowardsPlayer).toInt()
    val attackBase: Float get() = 0.1f * aggressiveTowardsBase
    val aiDecisionCooldown: Int get() = (500 / wisdom).toInt()
    val stuckTimeout: Int get() = (500 / agility).toInt()
    // After failing to find a path this many times, AI starts breaking thru bricks.
    val breakingBrickThreshold: Int get() = 2

    override fun toString(): String {
        return "AiPersonality[maniac=$maniac,aggressiveTowardsPlayer=$aggressiveTowardsPlayer]" +
                ",aggressiveTowardsBase=$aggressiveTowardsBase" +
                ",wisdom=$wisdom,agility=$agility]"
    }
}