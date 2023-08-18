package com.samwdev.battlecity.ui.screen

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.BattleResult
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.DebugConfig
import com.samwdev.battlecity.core.HandheldController
import com.samwdev.battlecity.core.NavEvent
import com.samwdev.battlecity.core.SoundEffect
import com.samwdev.battlecity.core.SoundPlayer
import com.samwdev.battlecity.core.StageCurtain
import com.samwdev.battlecity.core.TransitionToScoreboard
import com.samwdev.battlecity.core.plugInDebugConfig
import com.samwdev.battlecity.ui.component.AnimatedStageCurtain
import com.samwdev.battlecity.ui.component.BattleField
import com.samwdev.battlecity.ui.component.DebugConfigControlToggle
import com.samwdev.battlecity.ui.component.Hud
import com.samwdev.battlecity.ui.component.LocalBattleViewModel
import com.samwdev.battlecity.ui.component.RedGameOver
import kotlinx.coroutines.delay

@Composable
fun BattleScreen() {
    val battleViewModel: BattleViewModel = LocalBattleViewModel.current

    var showExitDialog by remember { mutableStateOf(false) }

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog = true
            }
        }
    }
    val localLifecycleOwner = LocalLifecycleOwner.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    DisposableEffect(localLifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(localLifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            battleViewModel.exit()
        }
    }

    if (battleViewModel.currentUIStatus < StageCurtain) return

    LaunchedEffect(battleViewModel.debugConfig) {
        battleViewModel.battle.plugInDebugConfig(battleViewModel.debugConfig)
    }

    CompositionLocalProvider(LocalDebugConfig provides battleViewModel.debugConfig) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Hud(
                    botCount = battleViewModel.mapState.remainingBot,
                    lifeCount = battleViewModel.gameState.player1.remainingLife,
                    level = battleViewModel.mapState.mapName,
                    modifier = Modifier.background(Color(117, 117, 117)),
                )

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)) {

                    BattleField(battleViewModel.battle, modifier = Modifier.fillMaxWidth())

                    if (battleViewModel.currentUIStatus == StageCurtain) {
                        LaunchedEffect(battleViewModel.currentStageName) {
                            delay(300)
                            SoundPlayer.INSTANCE.play(SoundEffect.StageStart)
                        }
                        AnimatedStageCurtain(
                            stageConfigPrev = battleViewModel.prevStageConfig,
                            stageConfigNext = battleViewModel.currStageConfig!!,
                        ) {
                            battleViewModel.start()
                        }
                    } else if (battleViewModel.currentUIStatus == TransitionToScoreboard
                        && battleViewModel.gameState.lastBattleResult == BattleResult.Lost) {
                        RedGameOver {
                            battleViewModel.showScoreboard()
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
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

        if (showExitDialog) {
            LaunchedEffect(Unit) {
                battleViewModel.pause()
            }

            AlertDialog(
                onDismissRequest = {
                    showExitDialog = false
                    battleViewModel.resume()
                },
                title = { Text("Exit the Game?") },
                text = { Text("Your progress will be lost") },
                confirmButton = {
                    Button(onClick = {
                        showExitDialog = false
                        battleViewModel.navigate(NavEvent.Up)
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showExitDialog = false
                        battleViewModel.resume()
                    }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

val LocalDebugConfig = compositionLocalOf { DebugConfig() }
