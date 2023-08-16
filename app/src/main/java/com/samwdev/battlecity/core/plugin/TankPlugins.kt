package com.samwdev.battlecity.core.plugin

import com.samwdev.battlecity.core.Tank
import com.samwdev.battlecity.core.TankSide

interface TankPlugin : Plugin<Tank>

class WhoIsYourDaddyPlugin : TankPlugin {
    override val identifier: String = "WhoIsYourDaddy"

    override fun transform(obj: Tank): Tank {
        return if (obj.side == TankSide.Player) {
            obj.copy(remainingShield = Int.MAX_VALUE)
        } else {
            obj
        }
    }

    override fun detransform(obj: Tank): Tank {
        return if (obj.side == TankSide.Player) {
            obj.copy(remainingShield = 0)
        } else {
            obj
        }
    }
}