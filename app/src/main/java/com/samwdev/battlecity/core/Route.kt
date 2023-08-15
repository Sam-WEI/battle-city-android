package com.samwdev.battlecity.core

object Route {
    const val Landing = "landing"
    const val MapSelection = "map_selection"
    const val BattleScreen = "battle"
    const val Scoreboard = "scoreboard"
    const val GameOver = "game_over"

    object Key {
        const val StageName = "stage_id"
    }
}

sealed interface NavEvent {
    object Up : NavEvent
    abstract class Routed(val route: String) : NavEvent
    object Landing : Routed(Route.Landing)
    object MapSelection : Routed(Route.MapSelection)
    object Scoreboard : Routed(Route.Scoreboard)
    object GameOver : Routed(Route.GameOver)
    data class BattleScreen(val stageName: String) : Routed("${Route.BattleScreen}/$stageName")
}