package com.samwdev.battlecity.core

const val SHOW_BRICK_INDEX: Boolean = false

data class DebugConfig(
    val showFps: Boolean = false,
    val showBrickIndex: Boolean = false,
    val showSteelIndex: Boolean = false,
    val showPivotBox: Boolean = false,
    val fixTickDelta: Boolean = false,
    val tickDelta: Int = 10,
    val maxFps: Int = TickState.MAX_FPS,
    val maxBot: Int = 2,
    val friendlyFire: Boolean = false,
)