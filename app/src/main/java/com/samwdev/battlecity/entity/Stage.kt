package com.samwdev.battlecity.entity

data class Stage(
    val name: String,
    val difficulty: Int,
    val map: List<String>,
    val bots: List<String>,
)
