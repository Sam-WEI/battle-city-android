package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samwdev.battlecity.core.BattleState
import com.samwdev.battlecity.core.BattleViewModel

@Composable
fun BattleField(battleState: BattleState, modifier: Modifier = Modifier) {
    TickAware(tickState = battleState.tickState) {
        Grid(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black),
            hGridUnitNum = battleState.mapState.hGridUnitNum,
            vGridUnitNum = battleState.mapState.vGridUnitNum,
        ) {
            BrickLayer(battleState.mapState.bricks)
            SteelLayer(battleState.mapState.steels)
            IceLayer(battleState.mapState.ices)
            WaterLayer(battleState.mapState.waters)
            EagleLayer(battleState.mapState.eagle)

            battleState.tankState.tanks.forEach { (_, tank) ->
                Tank(tank = tank)
            }

            Bullets(bullets = battleState.bulletState.bullets.values)

            TreeLayer(battleState.mapState.trees)

            battleState.explosionState.explosions.forEach { (_, explosion) ->
                Explosion(explosion = explosion)
            }

            battleState.powerUpState.powerUps.forEach { (_, powerUp) ->
                FlashingPowerUp(topLeft = Offset(powerUp.x, powerUp.y), powerUp = powerUp.type)
            }

            battleState.scoreState.onShowScreenScores.forEach { (_, score) ->
                OnScreenScore(onScreenScore = score)
            }

            if (LocalDebugConfig.current.showAccessPoints) {
                AccessPointLayer(mapState = battleState.mapState)
            }

            if (LocalDebugConfig.current.showWaypoints) {
                WaypointLayer(botState = battleState.botState)
            }
        }
    }
}
