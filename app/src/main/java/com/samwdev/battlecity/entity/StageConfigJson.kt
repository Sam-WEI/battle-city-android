package com.samwdev.battlecity.entity

import com.samwdev.battlecity.core.TankLevel

data class StageConfigJson(
    val name: String,
    val difficulty: Int,
    val map: List<String>,
    val bots: List<String>,
    val size: String,
)

data class StageConfig(
    val name: String,
    val difficulty: Int,
    val map: MapConfig,
    val bots: List<BotGroup>,
    // todo add spawn pos
)

data class MapConfig(
    val hGridUnitNum: Int,
    val vGridUnitNum: Int,
    val trees: Set<TreeElement>,
    val bricks: Set<BrickElement>,
    val steels: Set<SteelElement>,
    val waters: Set<WaterElement>,
    val ices: Set<IceElement>,
    val eagle: EagleElement,
)
data class BotGroup(
    val level: TankLevel,
    val count: Int,
)