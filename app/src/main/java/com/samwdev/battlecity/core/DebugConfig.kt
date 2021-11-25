package com.samwdev.battlecity.core

data class DebugConfig(
    val showFps: Boolean = false,
    val showBrickIndex: Boolean = false,
    val showSteelIndex: Boolean = false,
    val showPivotBox: Boolean = false,
    val showAccessPoints: Boolean = false,
    val fixTickDelta: Boolean = false,
    val tickDelta: Int = 10,
    val maxFps: Int = TickState.MAX_FPS,
    val maxBot: Int = 2,
    val friendlyFire: Boolean = false,
    val whoIsYourDaddy: Boolean = false,
)