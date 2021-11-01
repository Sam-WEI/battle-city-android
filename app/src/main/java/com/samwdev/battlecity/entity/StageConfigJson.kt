package com.samwdev.battlecity.entity

data class StageConfigJson(
    val name: String,
    val difficulty: Int,
    val map: List<String>,
    val bots: List<String>,
)

data class StageConfig(
    val name: String,
    val difficulty: Int,
    val map: MapElements,
    val bots: List<String>,
)

data class MapElements(
    val trees: List<Int> = listOf(),
    val bricks: List<Int> = listOf(),
    val steels: List<Int> = listOf(),
    val waters: List<Int> = listOf(),
    val ices: List<Int> = listOf(),
)
data class BotGroup(
    val level: BotTankLevel,
    val count: Int,
)