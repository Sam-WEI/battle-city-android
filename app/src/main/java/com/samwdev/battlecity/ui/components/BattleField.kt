package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.Battle

const val ZIndexTree = 10f
const val ZIndexPowerUp = 20f
const val ZIndexOnScreenScore = 30f

@Composable
fun BattleField(battle: Battle, modifier: Modifier = Modifier) {
    TickAware(tickState = battle.tickState) {
        Grid(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black),
            hGridSize = battle.mapState.hGridSize,
            vGridSize = battle.mapState.vGridSize,
        ) {
            BrickLayer(battle.mapState.bricks)
            SteelLayer(battle.mapState.steels)
            IceLayer(battle.mapState.ices)
            WaterLayer(battle.mapState.waters)
            EagleLayer(battle.mapState.eagle)
            TreeLayer(battle.mapState.trees)

            battle.tankState.tanks.forEach { (_, tank) ->
                Tank(tank = tank)
            }

            Bullets(bullets = battle.bulletState.bullets.values)

            battle.explosionState.explosions.forEach { (_, explosion) ->
                Explosion(explosion = explosion)
            }

            battle.powerUpState.powerUps.forEach { (_, powerUp) ->
                FlashingPowerUp(topLeft = Offset(powerUp.x, powerUp.y), powerUp = powerUp.type)
            }

            battle.scoreState.onShowScreenScores.forEach { (_, score) ->
                OnScreenScore(onScreenScore = score)
            }

            if (LocalDebugConfig.current.showAccessPoints) {
                AccessPointLayer(mapState = battle.mapState)
            }

            if (LocalDebugConfig.current.showWaypoints) {
                WaypointLayer(botState = battle.botState)
            }
        }
    }
}
