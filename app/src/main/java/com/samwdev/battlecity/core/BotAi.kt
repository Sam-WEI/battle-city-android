package com.samwdev.battlecity.core

class BotAi : TickListener {
    override fun onTick(tick: Tick) {

    }

    fun getCommand(): AiCommand {
        return Fire
    }
}

sealed class AiCommand()
object Fire : AiCommand()