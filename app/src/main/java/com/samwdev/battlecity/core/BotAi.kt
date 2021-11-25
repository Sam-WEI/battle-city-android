package com.samwdev.battlecity.core

import kotlin.random.Random
import kotlin.random.nextInt

private const val AiCd = 1000

class BotAi : TickListener {
    private var command: AiCommand = Stop

    private var cd: Int = AiCd

    fun getCommand(tank: Tank, accessPoints: AccessPoints): AiCommand {

        return Proceed
    }

    private fun findNewWaypoint(tank: Tank, accessPoints: AccessPoints) {

    }

    override fun onTick(tick: Tick) {
        if (cd > 0) {
            cd -= tick.delta.toInt()
            return
        }
        cd = AiCd
        when (Random.nextInt(0..100)) {
            in 0..40 -> {
                command = Fire
            }
            in 40..70 -> {
                command = Proceed
            }
            in 70..80 -> {
                command = Stop
            }
            in 80..85 -> {
                command = Turn(Direction.Up)
            }
            in 85..90 -> {
                command = Turn(Direction.Down)
            }
            in 90..95 -> {
                command = Turn(Direction.Right)
            }
            else -> {
                command = Turn(Direction.Left)
            }
        }
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