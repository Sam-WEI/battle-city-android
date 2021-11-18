package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.BattleState
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
import com.samwdev.battlecity.core.MapPixel
import com.samwdev.battlecity.core.grid2mpx

@Composable
fun BattleField(
    battleState: BattleState,
    modifier: Modifier = Modifier,
) {
    TickAware(tickState = battleState.tickState) {
        Map(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black)
        ) {
            BrickLayer(battleState.mapState.bricks)
            SteelLayer(battleState.mapState.steels)
            IceLayer(battleState.mapState.ices)
            WaterLayer(battleState.mapState.waters)
            EagleLayer(battleState.mapState.eagle)

            battleState.tankState.tanks.forEach { (id, tank) ->
                Tank(tank = tank)
            }

            battleState.bulletState.bullets.forEach { (id, bullet) ->
                Bullet(bullet)
            }

            TreeLayer(battleState.mapState.trees)

            SpawnBlink(topLeft = Offset(0f, 0f))
            SpawnBlink(topLeft = Offset(6f.grid2mpx, 0f))

            battleState.explosionState.explosions.forEach { (_, explosion) ->
                Explosion(explosion = explosion)
            }

            Text(
                text = "FPS ${battleState.tickState.fps}",
                color = Color.Green,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun Map(
    modifier: Modifier,
    sideBlockCount: Int = MAP_BLOCK_COUNT,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val mapPixelInDp = remember(maxWidth) { maxWidth / (sideBlockCount.grid2mpx) }
        CompositionLocalProvider(LocalMapPixelDp provides mapPixelInDp) {
            content()
        }
    }
}

/** MapPixel to Dp */
val MapPixel.mpx2dp: Dp @Composable get() = LocalMapPixelDp.current * this

/**
 * Provides dp size for one MapPixel
 */
val LocalMapPixelDp = staticCompositionLocalOf<Dp> {
    error("Not in Map composable or its child composable.")
}
