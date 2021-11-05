package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.BattleState
import com.samwdev.battlecity.core.HandheldController
import com.samwdev.battlecity.core.rememberBattleState
import com.samwdev.battlecity.entity.StageConfigJson

@Composable
fun BattleScreen(stageConfigJson: StageConfigJson) {
    val battleState: BattleState = rememberBattleState(stageConfigJson = stageConfigJson)

    LaunchedEffect(Unit) {
        battleState.startBattle()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BattleField(
            modifier = Modifier.fillMaxWidth(),
            battleState = battleState,
        )
        Box {
            HandheldController(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth(),
                handheldControllerState = battleState.handheldControllerState,
            )
            Text(
                text = "${battleState.handheldControllerState.direction}",
                color = Color.Green,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
    }
}