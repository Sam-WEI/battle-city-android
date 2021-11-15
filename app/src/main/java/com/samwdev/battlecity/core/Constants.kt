package com.samwdev.battlecity.core

const val BULLET_COLLISION_SIZE_IN_MAP_PIXEL: MapPixel = 3f

const val MAP_BLOCK_COUNT = 13

/** The original game pixel count in each block  */
private const val MAP_PIXEL_IN_EACH_BLOCK: MapPixel = 16f

const val TANK_MAP_PIXEL: MapPixel = MAP_PIXEL_IN_EACH_BLOCK

typealias MapPixel = Float

val Float.mpx: MapPixel get() = this * MAP_PIXEL_IN_EACH_BLOCK
val Int.mpx: MapPixel get() = this * MAP_PIXEL_IN_EACH_BLOCK.toFloat()