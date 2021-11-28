package com.samwdev.battlecity.entity

import com.samwdev.battlecity.core.TankLevel

data class StageConfigJson(
    val name: String,
    val difficulty: Int,
    val map: List<String>,
    val bots: List<String>,
    val width: Int?, // todo non-optional, better name
    val height: Int?,
)

data class StageConfig(
    val name: String,
    val difficulty: Int,
    val map: MapElements,
    val bots: List<BotGroup>,
)

data class MapElements(
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