package com.samwdev.battlecity.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.BattleResult
import com.samwdev.battlecity.core.TransitionToScoreboard
import com.samwdev.battlecity.core.BattleViewModel
import com.samwdev.battlecity.core.DebugConfig
import com.samwdev.battlecity.core.HandheldController
import com.samwdev.battlecity.core.StageCurtain

@Composable
fun BattleScreen() {
    val battleViewModel: BattleViewModel = LocalBattleViewModel.current

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // todo pause and prompt
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

    if (battleViewModel.currentUIStatus < StageCurtain) return

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
                    lifeCount = battleViewModel.gameState.player1.remainingLife,
                    level = battleViewModel.mapState.mapName,
                    modifier = Modifier.background(Color(117, 117, 117)),
                )

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)) {

                    BattleField(battleViewModel.battle, modifier = Modifier.fillMaxWidth())

                    if (battleViewModel.currentUIStatus == StageCurtain) {
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
    }
}

val LocalDebugConfig = compositionLocalOf { DebugConfig() }
