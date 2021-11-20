package com.samwdev.battlecity.core

const val SHOW_BRICK_INDEX: Boolean = false

data class DebugConfig(
    val showFps: Boolean = false,
    val showBrickIndex: Boolean = false,
    val showSteelIndex: Boolean = false,
    val showPivotBox: Boolean = false,
    val fixedTickDelta: Int = -1,
    val maxFps: Int = Int.MAX_VALUE,
)