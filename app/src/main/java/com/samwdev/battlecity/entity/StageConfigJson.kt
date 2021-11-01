package com.samwdev.battlecity.entity

import android.graphics.Point

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
    val trees: List<Int>,
    val bricks: List<Int>,
    val steels: List<Int>,
    val waters: List<Int>,
    val ices: List<Int>,
    val eagle: Point,
)
data class BotGroup(
    val level: BotTankLevel,
    val count: Int,
)