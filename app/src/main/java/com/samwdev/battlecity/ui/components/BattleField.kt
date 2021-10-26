package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.utils.logI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BattleField(gameState: GameState, modifier: Modifier = Modifier) {
    val tickState by gameState.tickState.tickFlow.collectAsState()
    LaunchedEffect(gameState.controllerState.direction) {
        // todo find another way
        snapshotFlow { gameState.controllerState.direction }
            .filter { it != Direction.Unspecified }
            .collect {
                logI("moving tank")
                gameState.tankState.direction = it
                when (it) {
                    Direction.Left -> gameState.tankState.x -= 1
                    Direction.Up -> gameState.tankState.y -= 1
                    Direction.Right -> gameState.tankState.x += 1
                    Direction.Down -> gameState.tankState.y += 1
                }
            }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color.Green)) {
        Text(text = "tick: ${gameState.tickState.uptimeMillis}. delta: ${gameState.tickState.delta}.")

        Tank(tank = gameState.tankState)
    }

}