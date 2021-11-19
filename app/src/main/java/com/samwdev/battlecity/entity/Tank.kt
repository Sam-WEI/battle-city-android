package com.samwdev.battlecity.entity

import com.samwdev.battlecity.core.TankLevel

enum class BotTankLevel(val level: TankLevel) {
    Basic(TankLevel.Level1),
    Fast(TankLevel.Level2),
    Power(TankLevel.Level3),
    Armor(TankLevel.Level4),
}
