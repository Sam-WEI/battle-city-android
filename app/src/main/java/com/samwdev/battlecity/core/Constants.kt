package com.samwdev.battlecity.core

/** Unit definition. One MapPixel stands for one original game's pixel */
typealias MapPixel = Float

const val BULLET_COLLISION_SIZE: MapPixel = 3f

/** Original game's map consists of 13 x 13 square cells */
const val MAP_GRID_SIZE: Int = 13

/** Side length of a grid cell in MapPixel */
private const val MAP_PIXEL_IN_EACH_GRID_CELL: MapPixel = 16f

/** Side length of a tank in MapPixel */
const val TANK_MAP_PIXEL: MapPixel = MAP_PIXEL_IN_EACH_GRID_CELL

val Float.toMpx: MapPixel get() = this
val Float.cell2mpx: MapPixel get() = this * MAP_PIXEL_IN_EACH_GRID_CELL
val Int.cell2mpx: MapPixel get() = this * MAP_PIXEL_IN_EACH_GRID_CELL