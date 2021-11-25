package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import com.samwdev.battlecity.utils.logE
import com.samwdev.battlecity.utils.logI
import kotlin.random.Random

class BotTankController(
    private val tankId: TankId,
    private val tankState: TankState,
    private val bulletState: BulletState, // todo move out?
    private val mapState: MapState,
) : TickListener {
//    val botAi: BotAi = BotAi()
    var currentWaypoint: List<SubGrid> by mutableStateOf(listOf(), referentialEqualityPolicy())
        private set

    override fun onTick(tick: Tick) {
        if (!tankState.isTankAlive(tankId)) {
            return
        }
//        botAi.onTick(tick)

        val tank = tankState.getTank(tankId)
        if (tank.isSpawning) {
            return
        }
        if (currentWaypoint.isEmpty()) {
            findNewWaypoint(tank, mapState.accessPoints)
        }

//        val command = botAi.getCommand()
        val distance = tank.maxSpeed * tick.delta

//        when (command) {
//            is Fire -> {
//                if (tank.remainingCooldown <= 0) {
//                    if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
//                        bulletState.fire(tank)
//                        tankState.startFireCooldown(tank.id)
//                    }
//                }
//            }
//            is Stop -> {
//                tankState.stopTank(tank.id)
//            }
//            is Turn -> {
//                tankState.moveTank(tank.id, command.direction)
//            }
//            is Proceed -> {
//                tankState.moveTank(tank.id, tank.movingDirection)
//            }
//        }
    }

    fun findNewWaypoint(tank: Tank, accessPoints: AccessPoints) {
        val dest = accessPoints.randomAccessiblePoint(Int.MAX_VALUE)
        val src = tank.pivotBox.topLeft.subGrid
        val waypoints = mutableListOf<SubGrid>()
        walkWaypointRecursive(waypoints, accessPoints, src, dest, null)
        currentWaypoint = waypoints
    }

    private fun walkWaypointRecursive(
        waypoints: MutableList<SubGrid>,
        accessPoints: AccessPoints,
        src: SubGrid,
        dest: SubGrid,
        currentDirection: Direction?,
    ): Boolean {
        logI(" src: ${src.subRow}, ${src.subCol}.  Dest: ${dest.subRow}, ${dest.subCol}")
        if (src == dest) {
            logE(" WOW!")
            waypoints.add(src)
            return true
        }
        if (src.isOutOfBound || accessPoints[src] < 0) {
            logE(" NO ENTRY!")
            return false
        }

        val directionAttemptPriority = mutableListOf<Direction>()
        if (dest.subRow != src.subRow && dest.subCol != src.subCol) {
            // decide to go vertically or horizontally first
            if (Random.nextBoolean()) {
                // go vertically first
                directionAttemptPriority.add(if (dest.subRow > src.subRow) Direction.Down else Direction.Up)
            } else {
                directionAttemptPriority.add(if (dest.subCol > src.subCol) Direction.Right else Direction.Left)
            }
        } else if (dest.subRow == src.subRow) {
            // horizontally equal, try vertically first
            directionAttemptPriority.add(if (dest.subCol > src.subCol) Direction.Right else Direction.Left)
        } else if (dest.subCol == src.subCol) {
            // vertically equal, try horizontally first
            directionAttemptPriority.add(if (dest.subRow > src.subRow) Direction.Down else Direction.Up)
        }
        val dirs = Direction.values().apply { shuffle() }
        dirs.forEach {
            if (it !in directionAttemptPriority) {
                directionAttemptPriority.add(it)
            }
        }
        currentDirection?.opposite?.let { directionAttemptPriority.remove(it) }
        logI(" turns: ${directionAttemptPriority.joinToString { it.toString() }}")

        for (dir in directionAttemptPriority) {
            val reached = walkWaypointRecursive(waypoints, accessPoints, src.getNeighborInDirection(dir), dest, dir)
            if (reached) {
                waypoints.add(src)
                return true
            }
        }
        return false
    }
}