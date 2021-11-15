package com.samwdev.battlecity.core

typealias MapPixel = Float

const val BULLET_COLLISION_SIZE: MapPixel = 3f

const val MAP_BLOCK_COUNT: Int = 13

/** The original game pixel count in each block  */
private const val MAP_PIXEL_IN_EACH_BLOCK: MapPixel = 16f

const val TANK_MAP_PIXEL: MapPixel = MAP_PIXEL_IN_EACH_BLOCK

val Float.toMpx: MapPixel get() = this
val Float.grid2mpx: MapPixel get() = this * MAP_PIXEL_IN_EACH_BLOCK
val Int.grid2mpx: MapPixel get() = this * MAP_PIXEL_IN_EACH_BLOCK