package com.samwdev.battlecity.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.samwdev.battlecity.utils.logE
import com.samwdev.battlecity.utils.logI
import kotlin.random.Random

private const val AiCd = 1000

class BotAi : TickListener {
    private var command: AiCommand = Stop

    private var cd: Int = AiCd

    var currentWaypoint: List<SubGrid> by mutableStateOf(listOf())
        private set

    fun getCommand(tank: Tank, accessPoints: AccessPoints): AiCommand {

        return Proceed
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

    override fun onTick(tick: Tick) {
//        if (cd > 0) {
//            cd -= tick.delta.toInt()
//            return
//        }
//        cd = AiCd
//        when (Random.nextInt(0..100)) {
//            in 0..40 -> {
//                command = Fire
//            }
//            in 40..70 -> {
//                command = Proceed
//            }
//            in 70..80 -> {
//                command = Stop
//            }
//            in 80..85 -> {
//                command = Turn(Direction.Up)
//            }
//            in 85..90 -> {
//                command = Turn(Direction.Down)
//            }
//            in 90..95 -> {
//                command = Turn(Direction.Right)
//            }
//            else -> {
//                command = Turn(Direction.Left)
//            }
//        }
    }

    fun getCommand(): AiCommand {
        return command
    }
}

sealed class AiCommand()
object Stop : AiCommand()
object Fire : AiCommand()
object Proceed : AiCommand()
data class Turn(val direction: Direction) : AiCommand()