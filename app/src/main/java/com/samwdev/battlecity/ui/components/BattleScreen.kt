package com.samwdev.battlecity.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@ExperimentalAnimationApi
@Composable
fun BattleScreen() {
    val battleViewModel: BattleViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

//    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.addCallback {
//    }

    if (battleViewModel.currentGameStatus < StageDataLoaded) return

    SideEffect {
        battleViewModel.tickState.maxFps = battleViewModel.debugConfig.maxFps
        battleViewModel.botState.maxBot = battleViewModel.debugConfig.maxBot
        battleViewModel.bulletState.friendlyFire = battleViewModel.debugConfig.friendlyFire
        battleViewModel.tankState.whoIsYourDaddy = battleViewModel.debugConfig.whoIsYourDaddy
        if (battleViewModel.debugConfig.fixTickDelta) {
            battleViewModel.tickState.fixTickDelta(battleViewModel.debugConfig.tickDelta)
        } else {
            battleViewModel.tickState.cancelFixTickDelta()
        }
    }

    CompositionLocalProvider(LocalDebugConfig provides battleViewModel.debugConfig) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Hud(
                    botCount = battleViewModel.mapState.remainingBot,
                    lifeCount = battleViewModel.mapState.remainingPlayerLife,
                    level = battleViewModel.mapState.mapName,
                    modifier = Modifier.background(Color(117, 117, 117)),
                )

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)) {

                    BattleField(battleViewModel.battleState, modifier = Modifier.fillMaxWidth())

                    if (battleViewModel.currentGameStatus == StageDataLoaded) {
                        StageCurtain(
                            stageConfigPrev = battleViewModel.prevStageConfig,
                            stageConfigNext = battleViewModel.currStageConfig!!,
                        ) {
                            battleViewModel.start()
                        }
                    } else if (battleViewModel.currentGameStatus == GameOver) {
                        RedGameOver {
                            battleViewModel.appState.navController.navigateUp()
                            battleViewModel.appState.navController.navigate(Route.Scoreboard)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    HandheldController(
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 60.dp)
                            .fillMaxWidth(),
                        onSteer = { dir -> battleViewModel.handheldControllerState.setSteerInput(dir) },
                        onFire =  { firing -> battleViewModel.handheldControllerState.setFireInput(firing) }
                    )

                    if (LocalDebugConfig.current.showFps) {
                        Text(
                            text = "FPS ${battleViewModel.tickState.fps}",
                            color = Color.Green,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }
            DebugConfigControlToggle(
                debugConfig = battleViewModel.debugConfig,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomEnd),
                onConfigChange = {
                    battleViewModel.debugConfig = it
                }
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
                    onSteer = {},
                    onFire = {},
                )
            }
        }
    }
}