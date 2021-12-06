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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import com.samwdev.battlecity.utils.Logger
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun BattleScreen() {
    val battleViewModel: BattleViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val composeCoroutineScope = rememberCoroutineScope()

    var debugConfig: DebugConfig by remember {
        mutableStateOf(DebugConfig(
            showFps = true,
            showPivotBox = false,
            maxBot = 4,
            showAccessPoints = false,
            showWaypoints = true,
        ))
    }

    LaunchedEffect(battleViewModel.currentGameStatus) {
        if (battleViewModel.currentGameStatus == MapCleared) {
            Logger.error(" read text")
        }
    }

    if (battleViewModel.currentGameStatus < StageDataLoaded) return

    SideEffect {
        battleViewModel.tickState.maxFps = debugConfig.maxFps
        battleViewModel.botState.maxBot = debugConfig.maxBot
        battleViewModel.bulletState.friendlyFire = debugConfig.friendlyFire
        battleViewModel.tankState.whoIsYourDaddy = debugConfig.whoIsYourDaddy
        if (debugConfig.fixTickDelta) {
            battleViewModel.tickState.fixTickDelta(debugConfig.tickDelta)
        } else {
            battleViewModel.tickState.cancelFixTickDelta()
        }
    }

    CompositionLocalProvider(LocalDebugConfig provides debugConfig) {
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
                debugConfig = debugConfig,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomEnd),
                onConfigChange = {
                    debugConfig = it
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