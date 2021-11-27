package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.samwdev.battlecity.core.BattleState
import com.samwdev.battlecity.core.grid2mpx

@Composable
fun Hud(battleState: BattleState, hudLayoutOrientation: Orientation = Orientation.Horizontal) {
    Box(
        modifier = Modifier
            .then(
                if (hudLayoutOrientation == Orientation.Horizontal) {
                    Modifier.fillMaxWidth().height(2f.grid2mpx.mpx2dp)
                } else {
                    Modifier.fillMaxHeight().width(2f.grid2mpx.mpx2dp)
                }
            )
    ) {

    }
}