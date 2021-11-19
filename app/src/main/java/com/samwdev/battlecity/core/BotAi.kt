package com.samwdev.battlecity.core

import kotlin.random.Random
import kotlin.random.nextInt

class BotAi : TickListener {
    private var command: AiCommand = Stop
    override fun onTick(tick: Tick) {
        when (Random.nextInt(0..100)) {
            in 0..60 -> {
                command = Fire
            }
            in 60..80 -> {
                command = Proceed
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