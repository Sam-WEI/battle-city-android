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
    val bots: List<BotGroup>,
)

data class MapElements(
    val trees: List<TreeElement>,
    val bricks: List<BrickElement>,
    val steels: List<SteelElement>,
    val waters: List<WaterElement>,
    val ices: List<IceElement>,
    val eagle: EagleElement,
)
data class BotGroup(
    val level: BotTankLevel,
    val count: Int,
)