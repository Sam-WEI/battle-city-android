package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samwdev.battlecity.core.BattleViewModel

@Composable
fun BattleField(
    modifier: Modifier = Modifier,
    battleViewModel: BattleViewModel = viewModel(),
) {
    TickAware(tickState = battleViewModel.tickState) {
        Grid(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black),
            hGridUnitNum = battleViewModel.mapState.hGridUnitNum,
            vGridUnitNum = battleViewModel.mapState.vGridUnitNum,
        ) {
            BrickLayer(battleViewModel.mapState.bricks)
            SteelLayer(battleViewModel.mapState.steels)
            IceLayer(battleViewModel.mapState.ices)
            WaterLayer(battleViewModel.mapState.waters)
            EagleLayer(battleViewModel.mapState.eagle)

            battleViewModel.tankState.tanks.forEach { (_, tank) ->
                Tank(tank = tank)
            }

            Bullets(bullets = battleViewModel.bulletState.bullets.values)

            TreeLayer(battleViewModel.mapState.trees)

            battleViewModel.explosionState.explosions.forEach { (_, explosion) ->
                Explosion(explosion = explosion)
            }

            battleViewModel.powerUpState.powerUps.forEach { (_, powerUp) ->
                FlashingPowerUp(topLeft = Offset(powerUp.x, powerUp.y), powerUp = powerUp.type)
            }

            battleViewModel.scoreState.onShowScreenScores.forEach { (_, score) ->
                OnScreenScore(onScreenScore = score)
            }

            if (LocalDebugConfig.current.showAccessPoints) {
                AccessPointLayer(mapState = battleViewModel.mapState)
            }

            if (LocalDebugConfig.current.showWaypoints) {
                WaypointLayer(botState = battleViewModel.botState)
            }
        }
    }
}
