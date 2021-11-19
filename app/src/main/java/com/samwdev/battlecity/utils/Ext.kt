package com.samwdev.battlecity.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.samwdev.battlecity.core.Direction
import com.samwdev.battlecity.core.MapPixel
import kotlin.math.max
import kotlin.math.min

fun Rect.getTravelPath(to: Rect): Rect {
    return Rect(
        left = min(left, to.left),
        top = min(top, to.top),
        right = max(right, to.right),
        bottom = max(bottom, to.bottom),
    )
}

fun Rect.move(distance: MapPixel, direction: Direction): Rect =
    when (direction) {
        Direction.Up -> translate(0f, -distance)
        Direction.Down -> translate(0f, distance)
        Direction.Left -> translate(-distance, 0f)
        Direction.Right -> translate(distance, 0f)
    }

fun Rect.moveUpTo(moveRestriction: MoveRestriction): Rect {
    return when (moveRestriction.direction) {
        Direction.Up -> Rect(offset = Offset(left, moveRestriction.bound), size = size)
        Direction.Down -> Rect(offset = Offset(left, moveRestriction.bound - height), size = size)
        Direction.Left -> Rect(offset = Offset(moveRestriction.bound, top), size = size)
        Direction.Right -> Rect(offset = Offset(moveRestriction.bound - width, top), size = size)
    }
}

data class MoveRestriction(val bound: MapPixel, val direction: Direction) {
    constructor(hitPoint: Offset, direction: Direction) :
            this (if (direction.isVertical()) hitPoint.y else hitPoint.x, direction)
    constructor(hitRect: Rect, direction: Direction) :
            this (
                when (direction) {
                     Direction.Up -> hitRect.bottom
                     Direction.Down -> hitRect.top
                     Direction.Left -> hitRect.right
                     Direction.Right -> hitRect.left
                 },
                direction
            )
}

fun List<MoveRestriction>.findMostRestrict(): MoveRestriction {
    require(isNotEmpty())
    val mostRestrict = when (first().direction) {
        Direction.Up, Direction.Left -> this.maxOf { it.bound }
        Direction.Down, Direction.Right -> this.minOf { it.bound }
    }
    return MoveRestriction(mostRestrict, first().direction)
}