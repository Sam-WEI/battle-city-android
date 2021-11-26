package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.collections.LinkedHashSet
import kotlin.math.abs
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

    override fun onTick(tick: Tick) {
        if (!tankState.isTankAlive(tankId)) {
            return
        }
        val tank = tankState.getTank(tankId)
        if (tank.isSpawning) {
            return
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
                    stuckTime += tick.delta.toInt()
                    if (stuckTime > 1000) {
                        findNewWaypoint(tank, mapState.accessPoints)
                        stuckTime = 0
                    }
                }
            }
        } else {
            findNewWaypoint(tank, mapState.accessPoints)

            val randomCmd = getNextCommand()
            when (randomCmd) {
                is Fire -> {
//                    if (tank.remainingCooldown <= 0) {
//                        if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
//                            bulletState.fire(tank)
//                            tankState.startFireCooldown(tank.id)
//                        }
//                    }
                }
                is Stop -> {
//                    tankState.stopTank(tank.id)
                }
                is Aggressive -> {
//                    tankState.moveTank(tank.id, )
                }
                is FindNewWaypoints -> {
//                    tankState.moveTank(tank.id, tank.movingDirection)
                }
            }
        }
    }

    private fun getNextCommand(): AiCommand {
        val random = Random.nextInt(commandWeightMap.values.sum())
        var acc = 0
        for ((cmd, weight) in commandWeightMap) {
            acc += weight
            if (random < weight) return cmd
        }
        return commandWeightMap.keys.last()
    }

    private val commandWeightMap: Map<AiCommand, Int> = mapOf(
        Stop to 1,
        FindNewWaypoints to 1,
        Fire to 1,
        Aggressive to 1,
    )

    private fun findNewWaypoint(tank: Tank, accessPoints: AccessPoints) {
        val dest = accessPoints.randomAccessiblePoint(Int.MAX_VALUE)
//        val dest = SubGrid(20, 0)
        val src = tank.pivotBox.topLeft.subGrid
//        val src = SubGrid(24, 24)
        val waypoints = LinkedHashSet<SubGrid>().apply { add(src) }
        walkWaypointRecursive(waypoints, mutableSetOf(), accessPoints, src, dest, null)
        currentWaypoint = waypoints.toList()
    }

    private fun walkWaypointRecursive(
        waypoints: LinkedHashSet<SubGrid>,
        badAccessPoints: MutableSet<SubGrid>,
        accessPoints: AccessPoints,
        src: SubGrid,
        dest: SubGrid,
        currentDirection: Direction?,
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
            if (nextWp.isOutOfBound) {
                continue
            }
            if (accessPoints[nextWp] < 0) {
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
            val reached = walkWaypointRecursive(waypoints, badAccessPoints, accessPoints, nextWp, dest, dir)
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

sealed class AiCommand()
object Stop : AiCommand()
object FindNewWaypoints : AiCommand()
object Fire : AiCommand()
object Aggressive : AiCommand()
