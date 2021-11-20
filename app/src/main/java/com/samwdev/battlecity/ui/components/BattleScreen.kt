package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.ui.theme.BattleCityTheme

@Composable
fun BattleScreen(stageConfigJson: StageConfigJson) {
    val battleState: BattleState = rememberBattleState(stageConfigJson = stageConfigJson)
    var debugConfig: DebugConfig by remember { mutableStateOf(DebugConfig(showFps = true)) }

    LaunchedEffect(Unit) {
        battleState.startBattle()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CompositionLocalProvider(LocalDebugConfig provides debugConfig) {
                BattleField(
                    modifier = Modifier.fillMaxWidth(),
                    battleState = battleState,
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                HandheldController(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 60.dp)
                        .fillMaxWidth(),
                    handheldControllerState = battleState.handheldControllerState,
                )
            }
        }

        DebugConfigControlToggle(
            modifier = Modifier.fillMaxWidth().height(400.dp).align(Alignment.BottomEnd),
            debugConfig = debugConfig,
            onConfigChange = { debugConfig = it }
        )
    }
}

val LocalDebugConfig = staticCompositionLocalOf { DebugConfig() }

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
            DebugConfigControlPanel(debugConfig = DebugConfig(), onConfigChange = {})
        }
    }
}