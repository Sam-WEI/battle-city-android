package com.samwdev.battlecity.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@ExperimentalAnimationApi
@Composable
fun BattleScreen(stageConfigJson: StageConfigJson) {
    val battleState: BattleState = rememberBattleState(stageConfigJson = stageConfigJson)
    var debugConfig: DebugConfig by remember {
        mutableStateOf(DebugConfig(
            showFps = true,
            showPivotBox = false,
            maxBot = 0,
            showAccessPoints = false,
            showWaypoints = false,
        ))
    }

    LaunchedEffect(Unit) {
        battleState.startBattle()
    }

    SideEffect {
        battleState.tickState.maxFps = debugConfig.maxFps
        battleState.botState.maxBot = debugConfig.maxBot
        battleState.bulletState.friendlyFire = debugConfig.friendlyFire
        battleState.tankState.whoIsYourDaddy = debugConfig.whoIsYourDaddy
        if (debugConfig.fixTickDelta) {
            battleState.tickState.fixTickDelta(debugConfig.tickDelta)
        } else {
            battleState.tickState.cancelFixTickDelta()
        }
    }

    CompositionLocalProvider(LocalDebugConfig provides debugConfig) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                BattleField(
                    modifier = Modifier.fillMaxWidth(),
                    battleState = battleState,
                )
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    HandheldController(
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 60.dp)
                            .fillMaxWidth(),
                        handheldControllerState = battleState.handheldControllerState,
                    )

                    if (LocalDebugConfig.current.showFps) {
                        Text(
                            text = "FPS ${battleState.tickState.fps}",
                            color = Color.Green,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }
            DebugConfigControlToggle(
                debugConfig = debugConfig,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomEnd),
                onConfigChange = { debugConfig = it }
            )
        }
    }
}

val LocalDebugConfig = compositionLocalOf { DebugConfig() }

@Preview
@Composable
private fun BattleStatePreview() {
    BattleCityTheme {
        Column(modifier = Modifier.size(500.dp, 500.dp)) {
            Box {
                HandheldController(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 60.dp)
                        .fillMaxWidth(),
                    handheldControllerState = HandheldControllerState(),
                )
            }
        }
    }
}